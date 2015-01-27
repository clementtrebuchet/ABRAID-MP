package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.junit.Test;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the WebServiceClient class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class WebServiceClientTest {
    private static final String GET_URL = "http://www.google.co.uk";

    // This is a POST data echo service
    private static final String POST_URL = "https://eu.httpbin.org/post";
    private static final String PUT_URL = "https://eu.httpbin.org/put";

    @Test
    public void makeGetRequestThrowsExceptionIfUnknownHost() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).makeGetRequest("http://uywnevoweiumoiunasdkjhaskjdhiouyncwiuec.be");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makeGetRequestThrowsExceptionIfMalformedURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).makeGetRequest("this is malformed");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makeGetRequestThrowsExceptionIfUnknownPage() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).makeGetRequest("http://www.google.co.uk/kjhdfgoiunewrpoimclsd");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makeGetRequestSuccessfullyGetsValidURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        String response = client.makeGetRequest(GET_URL);

        // Assert
        assertThat(response).containsIgnoringCase("google");
    }

    @Test
    public void makePostRequestWithJSONThrowsExceptionIfUnknownHost() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).makePostRequestWithJSON("http://uywnevoweiumoiunasdkjhaskjdhiouyncwiuec.be", "");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePostRequestWithJSONThrowsExceptionIfMalformedURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).makePostRequestWithJSON("this is malformed", "");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePostRequestWithJSONSuccessfullyPostsToValidURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient();
        String name = "Harry Hill";

        // Act
        String json = "{ \"name\": \"" + name + "\", \"age\": 49, \"dateOfBirth\": \"1964-10-01\" }";
        String response = client.makePostRequestWithJSON(POST_URL, json);

        // Assert
        assertThat(response).containsIgnoringCase("application/json");
        assertThat(response).containsIgnoringCase(name);
    }

    @Test
    public void makePostRequestWithXMLThrowsExceptionIfUnknownHost() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).makePostRequestWithXML("http://uywnevoweiumoiunasdkjhaskjdhiouyncwiuec.be", "");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePostRequestWithXMLThrowsExceptionIfMalformedURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).makePostRequestWithXML("this is malformed", "");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePostRequestWithXMLSuccessfullyPostsToValidURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient();
        String xml = "<test><name>Harry Hill</name></test>";

        // Act
        String response = client.makePostRequestWithXML(POST_URL, xml);

        // Assert
        assertThat(response).containsIgnoringCase("\"Content-Type\": \"text/xml\"");
        assertThat(response).containsIgnoringCase("\"data\": \"" + xml + "\"");
        assertThat(response).containsIgnoringCase("\"url\": \"" + POST_URL + "\"");
    }

    @Test
    public void makePutRequestWithXMLThrowsExceptionIfUnknownHost() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).makePutRequestWithXML("http://uywnevoweiumoiunasdkjhaskjdhiouyncwiuec.be", "");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePutRequestWithXMLThrowsExceptionIfMalformedURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).makePutRequestWithXML("this is malformed", "");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePutRequestWithXMLSuccessfullyPostsToValidURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient();
        String xml = "<test><name>Harry Hill</name></test>";

        // Act
        String response = client.makePutRequestWithXML(PUT_URL, xml);

        // Assert
        assertThat(response).containsIgnoringCase("\"Content-Type\": \"text/xml\"");
        assertThat(response).containsIgnoringCase("\"data\": \"" + xml + "\"");
        assertThat(response).containsIgnoringCase("\"url\": \"" + PUT_URL + "\"");
    }


    @Test
    public void makePostRequestWithByteArrayThrowsExceptionIfUnknownHost() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).makePostRequestWithBinary("http://uywnevoweiumoiunasdkjhaskjdhiouyncwiuec.be", new byte[]{});

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePostRequestWithByteArrayThrowsExceptionIfMalformedURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).makePostRequestWithBinary("this is malformed", new byte[]{});

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePostRequestWithByteArraySuccessfullyPostsToValidURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient();
        String bodyAsString = "Test body";

        // Act
        String response = client.makePostRequestWithBinary(POST_URL, bodyAsString.getBytes());

        // Assert
        assertThat(response).containsIgnoringCase("application/octet-stream");
        assertThat(response).containsIgnoringCase(bodyAsString);
    }
}
