package com.intervueai.backend.resume.parser;

import java.io.InputStream;

public interface ResumeParser {

    String parse(InputStream inputStream);
}