package org.aksw.iana.language.subtag.registry.rdfizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.aksw.iana.subtag.registry.domain.IanaSubTag;
import org.aksw.jena_sparql_api.mapper.proxy.JenaPluginUtils;
import org.aksw.jena_sparql_api.rx.op.FlowableOperatorSequentialGroupBy;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.system.stream.StreamManager;
import org.apache.jena.riot.web.LangTag;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import io.reactivex.rxjava3.core.Flowable;

public class MainIanaSubTagRegistryExporter {


	public static void main(String[] args) throws Exception {
		JenaPluginUtils.registerResourceClasses(IanaSubTag.class);
		

		//URL url = new URL("http://www.iana.org/assignments/language-subtag-registry/language-subtag-registry");

		Model model = IanaLanguageSubtagRegistryRdfizer.rdfize("http://www.iana.org/assignments/language-subtag-registry/language-subtag-registry");
		
		Multimap<Integer, String> index = langTagKnownValues(model);
		
		
		validate("de-jp", index);
		
		// RDFDataMgr.write(System.out, model, RDFFormat.TURTLE_PRETTY);
	}

	public static boolean validate(String langTag, Multimap<Integer, String> index) {
		String[] parts = LangTag.parse(langTag);
		System.out.println(Arrays.toString(parts));

		int[] knownIdxs = new int[] {LangTag.idxLanguage, LangTag.idxScript, LangTag.idxRegion, LangTag.idxVariant, LangTag.idxExtension };
		
		if (parts == null) {
			throw new RuntimeException("Failed to parse: " + langTag);
		} else {
			for (int i = 0; i < knownIdxs.length; ++i) {
				int partId = knownIdxs[i];
				String givenValue = parts[partId];
				
				if (givenValue == null) {
					continue;
				}
				
				Collection<String> knownValidValues = index.get(partId); 				
				
				boolean isValidValue = knownValidValues.contains(givenValue);
				
				if (!isValidValue) {
					throw new RuntimeException("Value '" + givenValue + "' is not know to be valid for part #" + partId + " valid values: " + new TreeSet<>(knownValidValues));
				}
			}
		}

		return true;
	}
	
	public static Multimap<Integer, String> langTagKnownValues(Model langRegistryModel) {
		Multimap<Integer, String> result = HashMultimap.create();
		
		Map<String, Integer> idxToType = new HashMap<>();
		idxToType.put("language", LangTag.idxLanguage);
		idxToType.put("script", LangTag.idxScript);
		idxToType.put("region", LangTag.idxRegion);
		idxToType.put("variant", LangTag.idxVariant);
		idxToType.put("extlang", LangTag.idxExtension);
		
		Property type = ResourceFactory.createProperty("urn:Type");
		
		Set<IanaSubTag> set = langRegistryModel.listSubjectsWithProperty(type)
				.mapWith(r -> r.as(IanaSubTag.class)).toSet();
		
		for (IanaSubTag tag : set) {
			String xtype = tag.getType();
			String xsubtag = tag.getSubtag();
			Integer idx = idxToType.get(xtype);

			if (idx == null) {
				System.err.println("Unknown type: " + xtype + " on " + tag);
				// Objects.requireNonNull(idx, "Failed to map " + tag);
				continue;
			}
			
			result.put(idx, xsubtag);			
		}

		return result;
	}
}
