package org.aksw.iana.language.subtag.registry.rdfizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;

import org.aksw.commons.rx.op.FlowableOperatorSequentialGroupBy;
import org.aksw.iana.subtag.registry.domain.IanaSubTag;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.system.stream.StreamManager;
import org.apache.jena.util.ResourceUtils;

import com.github.jsonldjava.shaded.com.google.common.base.CaseFormat;

import io.reactivex.rxjava3.core.Flowable;

public class IanaLanguageSubtagRegistryRdfizer {
    public static final String CONTINUATION_MARKER = "  ";


    public static Model rdfize(String filenameOrUrl) throws IOException {
        try (InputStream in = StreamManager.get().open(filenameOrUrl)) {
            return rdfize(in);
        }
    }

    public static Model rdfize(InputStream in) {
        Iterable<String> it = () -> new BufferedReader(new InputStreamReader(in)).lines().iterator();

        return rdfize(it);
    }

    public static Model rdfize(Iterable<String> it) {
        long recordId[] = {0};
        Flowable<Resource> flow = Flowable.fromIterable(it)
            // Handle record boundaries
            .filter(line -> {
                boolean isEndOfRecord = line.equals("%%");
                if (isEndOfRecord) {
                    ++recordId[0];
                }
                return !isEndOfRecord;
            })
            .lift(FlowableOperatorSequentialGroupBy.<String, Long, LinkedList<String>>create(
                    line -> recordId[0], groupkey -> new LinkedList<String>(), Collection::add))
            .map(Entry::getValue)
            // Handle line breaks in values
            .map(lines -> {
                ListIterator<String> lit = lines.listIterator(lines.size());
                while (lit.hasPrevious()) {
                    String line = lit.previous();
                    if (line.startsWith(CONTINUATION_MARKER)) {
                        String part = line.substring(CONTINUATION_MARKER.length()).trim();
                        String prev = lit.previous();
                        lit.set(prev + " " + part);
                        lit.next();
                        lit.next();
                        lit.remove();
                    }
                }

                return lines;
            })
            // Apply generic RDFization
            .map(lines -> {
                Resource r = ModelFactory.createDefaultModel().createResource();
                for (String line : lines) {
                    String[] kv = line.split(":", 2);
                    if (kv.length == 1) {
                        throw new RuntimeException("Should not happen: Non-key-value line: " + line);
                    }

                    String p = kv[0].trim();
                    r.addLiteral(ResourceFactory.createProperty("urn:x-key:" + p), kv[1].trim());
                }
                return r;
            // Skolemize
            }).map(in -> {
                IanaSubTag subTag = in.as(IanaSubTag.class);
                String iri = "urn:x-iana:" + subTag.getType() + "." + subTag.getSubtag();
                Resource out = ResourceUtils.renameResource(in, iri);
                return out;
            })
            ;

        Model result = ModelFactory.createDefaultModel();
        flow.forEach(r -> result.add(r.getModel()));

        return result;
    }
}
