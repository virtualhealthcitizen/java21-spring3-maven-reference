package org.squidmin.java.spring.maven.casestudies.casestudy1.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.squidmin.java.spring.maven.casestudies.casestudy1.repository.GcsRepository;

@Repository
public class GcsRepositoryImpl implements GcsRepository {

    private static final Logger log = LoggerFactory.getLogger(GcsRepositoryImpl.class);

}
