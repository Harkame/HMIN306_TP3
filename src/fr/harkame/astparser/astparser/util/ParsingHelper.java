package fr.harkame.astparser.astparser.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ParsingHelper
{
	public static String fileToString(String filePath) throws IOException
	{
		StringBuilder fileCode = new StringBuilder(1000);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));

		char[] buffer = new char[10];
		int numRead = 0;
		while ((numRead = bufferedReader.read(buffer)) != -1)
		{
			String readData = String.valueOf(buffer, 0, numRead);
			fileCode.append(readData);
			buffer = new char[1024];
		}

		bufferedReader.close();

		return fileCode.toString();
	}
}
