/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.adobe.training.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@SuppressWarnings("serial")
@SlingServlet(resourceTypes = "trainingprojecr/structure/page" , paths ="/bin/test")
public class SimpleServletUsingPath extends SlingSafeMethodsServlet {
	   private final Logger logger = LoggerFactory.getLogger(getClass());
	   
    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
    	 try
         {
         	logger.info("---> THIS IS THE GET METHOD OF slingSevletApp/components/page/slingTemplate");
             PrintWriter out =             resp.getWriter();
             out.println("<html><body>");
             out.println("<h1>This value SimpleServletUsingPath was returned by an AEM Sling Servlet bound by using a Sling ResourceTypes property</h1>");
             out.println("</html></body>");
              
         }
         catch(Exception e)
         {
         	logger.info(e.getMessage(),e);
         }
    }
}
