package org.squidmin.java.spring.maven.gcs.service;

import org.squidmin.java.spring.maven.gcs.dto.ExampleRequest;

import java.io.IOException;
import java.net.URL;

public interface GcsService {

    URL uploadAvro(String filename, ExampleRequest request) throws IOException;

    URL downloadAvro(String filename) throws IOException;



}
