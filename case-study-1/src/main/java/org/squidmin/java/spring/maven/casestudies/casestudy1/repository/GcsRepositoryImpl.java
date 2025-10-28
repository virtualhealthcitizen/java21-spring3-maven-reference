package org.squidmin.java.spring.maven.casestudies.casestudy1.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class GcsRepositoryImpl implements GcsRepository {

    private static final Logger log = LoggerFactory.getLogger(GcsRepositoryImpl.class);

}
