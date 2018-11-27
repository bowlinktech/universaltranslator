/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class NoHtmlValidator implements ConstraintValidator<NoHtml, String> {

    /* http://owasp-java-html-sanitizer.googlecode.com/svn/trunk/distrib/javadoc/org/owasp/html/HtmlPolicyBuilder.html */
 /* builder is not thread safe, so make local */
    private static final PolicyFactory DISALLOW_ALL = new HtmlPolicyBuilder()
            .allowStandardUrlProtocols()
            .toFactory();

    @Override
    public void initialize(NoHtml constraintAnnotation) {
        // TODO specify the policy as an annotation attribute
        // to use them, values from annotation are stored in private properties here
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        } else {
            String sanitized = DISALLOW_ALL.sanitize(value);

            //Need to replace &#64; back to @
            sanitized = sanitized.replace("&#64;", "@").replace("&amp;", "&").replace("&#39;", "'").replace("&#34;", "\"").replace("&#43;", "+");

            return sanitized.equals(value);
        }

    }
}
