package org.fao.geonet.api.records.editing;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.fao.geonet.services.AbstractServiceIntegrationTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javassist.NotFoundException;
import jeeves.server.context.ServiceContext;

public class InspireValidatorUtilsTest extends AbstractServiceIntegrationTest {

    private static String URL = "http://inspire-sandbox.jrc.ec.europa.eu/etf-webapp";

    @Autowired
    InspireValidatorUtils inspireValidatorUtils;

    private ServiceContext context;

    @Before
    public void setUp() throws Exception {
        this.context = createServiceContext();
    }

    @Test
    public void testGetReportUrl() {

        String reportUrl = inspireValidatorUtils.getReportUrl(URL, "123");

        assertEquals(URL + "/v2/TestRuns/123.html", reportUrl);
    }

    @Test
    public void testGetReportUrlJSON() {

        String reportUrl = inspireValidatorUtils.getReportUrlJSON(URL, "123");

        assertEquals(URL + "/v2/TestRuns/123.json", reportUrl);
    }

    @Test
    @Ignore
    public void testLifeCycle() throws IOException {

        assertEquals(inspireValidatorUtils.checkServiceStatus(context, "http://wrong.url.eu"), false);

        // FIRST TEST IF OFFICIAL ETF IS AVAILABLE
        // Needed to avoid GN errors when ETF is not available
        if (inspireValidatorUtils.checkServiceStatus(context, URL)) {

            try {
                // No file
                inspireValidatorUtils.submitFile(context, URL, null, "Metadata (TG version 1.3)", "GN UNIT TEST ");
            } catch (IllegalArgumentException e) {
                // RIGHT EXCEPTION
            } catch (Exception e) {
                assertEquals("Wrong exception.", "IllegalArgumentException", "Exception");
            }

            try {
                // Valid but not found test ID
                inspireValidatorUtils.isReady(context, URL, "IED123456789012345678901234567890123");
                assertEquals("No exception!", "NotFoundException", "No Exception");
            } catch (NotFoundException e) {
                // RIGHT EXCEPTION
            } catch (Exception e) {
                assertEquals("Wrong exception.", "NotFoundException", "Exception");
            }

            try {
                // Test ID in wrong format
                assertEquals(inspireValidatorUtils.isPassed(context, URL, "1"), null);
            } catch (Exception e) {
                assertEquals("Unexpected exception.", "Exception", "No Exception");
            }

        } else {
            assertEquals("The official ETF endpoint is not available. Can't run further tests.", URL, URL);
        }

    }


}

