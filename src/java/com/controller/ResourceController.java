/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller;

import com.helper.Resource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author BXDN
 */
@ManagedBean
@ViewScoped
public class ResourceController implements Serializable {

    List<Resource> resources = new ArrayList<>();
    Resource resource = new Resource();

    public List<Resource> getResources() {
        resources = getResourceName();
        return resources;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
    
    /**
     * Creates a new instance of EpisodeController
     */
    public ResourceController() {
    }

    public String getResourceByKeyWord(String keyWord) {
        this.resources = null;
        String query = "SELECT ?name ?description ?birthDate ?birthPlace ?deathDate ?deathPlace ?surname ?thumbnail WHERE {"
                + "<" + keyWord + "> <http://xmlns.com/foaf/0.1/name> ?name ."
                + "OPTIONAL {"
                + "<" + keyWord + "> <http://xmlns.com/foaf/0.1/surname> ?surname. "
                + "<" + keyWord + "> <http://purl.org/dc/elements/1.1/description> ?description ."
                + "<" + keyWord + "> <http://dbpedia.org/ontology/birthDate> ?birthDate ."
                + "<" + keyWord + "> <http://dbpedia.org/ontology/birthPlace> ?birthPlace ."
                + "<" + keyWord + "> <http://dbpedia.org/ontology/deathDate> ?deathDate ."
                + "<" + keyWord + "> <http://dbpedia.org/ontology/deathPlace> ?deathPlace."
                + "<" + keyWord + "> <http://dbpedia.org/ontology/thumbnail> ?thumbnail."
                + "}}"
                //+ " GROUP BY ?resource ?name ?description ?birthDate ?birthPlace ?deathDate ?deathPlace ?surname"
                + " LIMIT 1";

        Query local = QueryFactory.create(query);
        System.out.println(query);
        // initializing queryExecution factory with remote service.
        // **this actually was the main problem I couldn't figure out.**

        //QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", local);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://localhost:3030/ds/query", local);
        //after it goes standard query execution and result processing which can
        // be found in almost any Jena/SPARQL tutorial.
        try {
            ResultSet results = qexec.execSelect();
            while (results != null && results.hasNext()) {
                QuerySolution querySolution = results.nextSolution();
                String name = querySolution.get("name") + "";
                String givenName = querySolution.get("givenName") + "";
                String surname = querySolution.get("surname") + "";
                String description = querySolution.get("description") + "";
                String birthDate = querySolution.get("birthDate") + "";
                String birthPlace = querySolution.get("birthPlace") + "";
                String deathDate = querySolution.get("deathDate") + "";
                String deathPlace = querySolution.get("deathPlace") + "";
                String thumbnail = querySolution.get("thumbnail") + "";

                //Resource res = new Resource();
                resource.setName(name);
                resource.setGivenName(givenName);
                resource.setSurname(surname);
                resource.setDescription(description);
                resource.setBirthDate(birthDate);
                resource.setBirthPlace(birthPlace);
                resource.setDeathDate(deathDate);
                resource.setDeathPlace(deathPlace);
                resource.setRessource(keyWord);
                resource.setRessource(thumbnail);
            }
        } finally {
            qexec.close();
        }
        return "item";
    }

    public List<Resource> getResourceName() {
        resources = new ArrayList<>();
        String query = "SELECT DISTINCT ?resource ?name WHERE {"
                + "?resource ?y ?z ."
                + "?resource <http://xmlns.com/foaf/0.1/name> ?name ."                
                + "}"
                + " ORDER BY ?name "
                + " LIMIT 200";

        Query local = QueryFactory.create(query);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://localhost:3030/ds/query", local);
        try {
            ResultSet results = qexec.execSelect();
            while (results != null && results.hasNext()) {
                QuerySolution querySolution = results.nextSolution();
                String name = querySolution.get("name") + "";
                String r = querySolution.get("resource") + "";
                Resource res = new Resource();
                res.setName(replaceLanguage(name));
                res.setRessource(r);
                resources.add(res);               
            }
        } finally {
            qexec.close();
        }
        return resources;
    }

    String replaceLanguage(String str) {
        String replace = str.replace("@fr", "");
        return replace;
    }
}
