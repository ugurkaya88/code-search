package com.ugurkaya.services.rest.search.code.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;

import com.ugurkaya.services.rest.search.code.definition.RequestConstants;

/**
 * Utility class to enhance http connection experience.
 * 
 * @author Ugur
 *
 */
public final class HttpConnectionUtilities {
	/**
	 * 
	 * Creates an HttpURLConnection from the URI and the parameters.
	 * 
	 * @param uri
	 * @param parameterMap
	 * @return HttpURLConnection with the specified query parameters attached
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static HttpURLConnection getHttpURLConnection(String uri, Map<String, String> parameterMap) throws MalformedURLException, IOException {
		return getHttpURLConnection(uri, parameterMap, false);
	}

	/**
	 * 
	 * Creates an HttpURLConnection from the URI according to the redirected parameter.
	 * 
	 * @param uri
	 * @param redirected
	 * @return HttpURLConnection with the specified URI
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static HttpURLConnection getHttpURLConnection(String uri, boolean redirected) throws MalformedURLException, IOException {
		return getHttpURLConnection(uri, null, redirected);
	}

	/**
	 * 
	 * Creates an HttpURLConnection from the URI.
	 * 
	 * @param uri
	 * @return HttpURLConnection with the specified URI
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static HttpURLConnection getHttpURLConnection(String uri) throws MalformedURLException, IOException {
		return getHttpURLConnection(uri, null, false);
	}

	/**
	 * 
	 * Creates an HttpURLConnection from the URI and the query parameters according the redirected parameter.
	 * 
	 * @param uri
	 * @param parameterMap
	 * @param redirected
	 * @return HttpURLConnection with the specified query parameters attached
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static HttpURLConnection getHttpURLConnection(String uri, Map<String, String> parameterMap, boolean redirected) throws MalformedURLException, IOException {
		String completeURI;
		
		// If redirected, then do not append the parameters,
		// Otherwise; append the parameters if there are any.
		if (redirected) {
			completeURI = uri;
		} else {
			String queryParametersString = stringifyParameters(parameterMap);

			completeURI = uri + (!queryParametersString.isEmpty() ? "?" + queryParametersString : "");
		}

		// Opens the connection.
		HttpURLConnection connection = (HttpURLConnection) new URL(completeURI).openConnection();
		connection.setRequestProperty("User-Agent", RequestConstants.DEFAULT_USER_AGENT);

		return connection;
	}

	/**
	 * 
	 * Converts the <Key, Value> map to a String representation.
	 * 
	 * @param parameterMap
	 * @return String representation of the <Key, Value> map
	 */
	private static String stringifyParameters(Map<String, String> parameterMap) {
		StringBuilder stringBuilder = new StringBuilder();

		if (parameterMap != null) {
			parameterMap.forEach((key, value) -> stringBuilder.append("&" + key + "=" + value));
		}

		return stringBuilder.length() > 0 ? stringBuilder.substring(1) : "";
	}

	/**
	 * 
	 * Gets the response from an HttpURLConnection.
	 * 
	 * If the response code indicates a non-failure code, in other words 200 <= responseCode < 400,
	 * 		If the response code targets to a moved resource, in other words 301, 302 or 303, do redirect and return the result from the redirected connection,
	 * 		Otherwise; return the result from the current connection,
	 * Otherwise; return the error result from the current connection.
	 * 
	 * After deciding the input stream (InputStream or ErrorStream), reads the result from the stream and returns the data read along with a success code.
	 * Success code is decided according to the input stream selection;
	 * 		True  => Data is read from the InputStream
	 * 		False => Data is read from the ErrorStream
	 * 
	 * @param connection
	 * @param redirected
	 * @return Entry<Boolean, String> containing the connection success result and the data read from the stream
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static Entry<Boolean, String> getConnectionResponse(HttpURLConnection connection, boolean redirected) throws MalformedURLException, IOException {
		int statusCode = connection.getResponseCode();
		boolean success;

		InputStream inputStream;
		if (statusCode >= HttpURLConnection.HTTP_OK && statusCode < HttpURLConnection.HTTP_BAD_REQUEST) {
			if (statusCode == HttpURLConnection.HTTP_MOVED_PERM || statusCode == HttpURLConnection.HTTP_MOVED_TEMP || statusCode == HttpURLConnection.HTTP_SEE_OTHER) {
				HttpURLConnection redirectedConnection = HttpConnectionUtilities.getHttpURLConnection(connection.getHeaderField("Location"), true);
				System.out.println("INFO: " + "Redirect.. From => " + connection.getURL());
				System.out.println("INFO: " + "Redirect.. To   => " + redirectedConnection.getURL());
				return getConnectionResponse(redirectedConnection, true);
			}
			
			inputStream = connection.getInputStream();
			success = true;
		} else {
			inputStream = connection.getErrorStream();
			success = false;
		}

		StringBuilder stringBuilder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
			for (String line; (line = reader.readLine()) != null;) {
				stringBuilder.append(line);
			}
		}

		return new AbstractMap.SimpleEntry<Boolean, String>(success, stringBuilder.toString());
	}
}
