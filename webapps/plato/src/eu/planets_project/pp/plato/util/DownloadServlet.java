/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.planets_project.pp.plato.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.DigitalObject;

/**
 * Download servlet which is used for downloading the files which have been uploaded by the user.
 *
 * @author Hannes Kulovits
 */
public class DownloadServlet extends HttpServlet {

    private static final long serialVersionUID = -1384674829569057569L;

    public void init() {

    }

    /**
     * Servlets doGet method.
     *
     * Depending on the parameter <code>fileId</code> the respective file of the currently selected
     * project will be downloaded. <code>fileId</code>
     * can be:
     *
     * <ul>
     * <li><code>mandate</code> starts download for <code>projectBasis.mandateUpload</code></li>
     * <li><code>community</code> starts download for <code>projectBasis.designatedCommunityUpload</code></li>
     * <li><code>policy</code> starts download for <code>projectBasis.applyingPoliciesUpload</code></li>
     * <li><code>report</code> starts download for <code>planProperties.reportUpload</code></li>
     * </ul>
     *
     * In case any error occurs the user is forwarded to the Load Plan page.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException{

        HttpSession session = request.getSession(true);

        DigitalObject requestedFile = null;

        String fileId = request.getParameter("fileId");

        if (fileId.equalsIgnoreCase("mandate")
                || fileId.equalsIgnoreCase("community")
                ||fileId.equalsIgnoreCase("policy")) {

            requestedFile = getFile(fileId, request, response);
        } else if (fileId.equalsIgnoreCase("report")) {

            IDownloadManagerHelperBean mgr = (IDownloadManagerHelperBean)session.getAttribute("downloadManagerHelperBean");

            if (mgr == null) {
                response.sendRedirect("/plato/project/loadPlan.seam");
                return;
            }

            String ppId = request.getParameter("ppId");
            requestedFile = mgr.getUploadedReportFile(ppId);
        }

        if (requestedFile == null) {
            response.sendRedirect("/plato/project/loadPlan.seam");
            return;
        }

        response.setHeader("Content-Disposition", "attachment;filename=\""
                + requestedFile.getFullname() + "\"");
        response.setContentLength((int) requestedFile.getData().getData().length);
        response.setContentType(requestedFile.getContentType());

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(requestedFile.getData().getData());
            OutputStream out = response.getOutputStream();

            // Copy the contents of the file to the output stream
            byte[] buf = new byte[1024];
            int count;
            while ((count = in.read(buf)) >= 0) {
                out.write(buf, 0, count);
            }
            in.close();
            out.flush();
            out.close();
        } catch (IOException ex) {
            System.out.println("Error in downloadFile: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Returns the DigitalObject object of the respective file.
     *
     * @param fileId For possible file ids see class description.
     * @return DigitalObject object
     */
    private DigitalObject getFile(String fileId, HttpServletRequest request, HttpServletResponse response) {

        if (! "mandate".equals(fileId)
                && !"community".equals(fileId)
                && !"policy".equals(fileId)) {

            return null;
        }

        try {

            HttpSession session = request.getSession(true);

            Plan selectedPlan = (Plan)session.getAttribute("selectedPlan");

            if (selectedPlan == null) {
                response.sendRedirect("/plato/project/loadPlan.seam");
                return null;
            }


            DigitalObject requestedFile = null;

            /*
            if (fileId.equalsIgnoreCase("mandate")) {
                requestedFile = selectedPlan.getProjectBasis().getMandateUpload();
            } else if (fileId.equalsIgnoreCase("community")) {
                requestedFile = selectedPlan.getProjectBasis().getDesignatedCommunityUpload();
            } else if (fileId.equalsIgnoreCase("policy")) {
                requestedFile = selectedPlan.getProjectBasis().getApplyingPoliciesUpload();
            }
            */

            return requestedFile;

        } catch(IOException e) {
            return null;
        }

    }
}
