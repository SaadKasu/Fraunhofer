import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.example.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStream

class TestURLDatabaseSystem {

  var urlDatabaseSystem : URLDatabaseSystem = URLDatabaseSystem()
  var inputURLArrayForStaticCases : List<String> = emptyList()
  var inputTechnologiesArrayForStaticCases : List<String> = emptyList()
  var outputTechnologiesArrayForStaticCases : List<String> = emptyList()
  var expectedFileOutput : List<String> = emptyList()
  var inputFilePath : String = ""
  var outputFilePath : String = ""

  fun setUpTestDataForStaticCases(){                                                                                    // Set up for data provided as an input array.

   urlDatabaseSystem = URLDatabaseSystem()
   inputURLArrayForStaticCases = listOf<String>("https://upb.de/site/",
       "https://upb.de/site/drupal/", "https://upb.de/site/drupal/g",
       "https://upb.de/site/jml","https://upb.de/site/jml/pdfs",
       "https://upb.de/site/jml/pdfs/123")
   inputTechnologiesArrayForStaticCases = listOf<String>("Apache, PHP5",
       "Drupal, Apache, PHP5, RedHat",
       "Drupal, Apache, PHP5, RedHat, AngularJS",
       "Joomla, Apache, PHP4, AngularJS",
       "PDF generator","")
   outputTechnologiesArrayForStaticCases = listOf<String>("Apache, PHP5",
       "Drupal, RedHat",
       "AngularJS",
       "Joomla, PHP4, AngularJS",
       "PDF generator",
       "")

  }

  fun setUpTestDataForFileCases(){                                                                                      // Set up for File data. With file path and expected file output provided.

   urlDatabaseSystem = URLDatabaseSystem()
   inputFilePath = "src/test/resources/InputData.csv"
   outputFilePath = "src/test/resources/OutputData.csv"
   expectedFileOutput = listOf<String>("URL,Technologies,Output Technologies",
       "https://upb.de/site/,\"Apache, PHP5\",\"Apache, PHP5\"",
       "https://upb.de/site/drupal/,\"Drupal, Apache, PHP5, RedHat\",\"Drupal, RedHat\"",
       "No Input",
       "https://upb.de/site/drupal/g,\"Drupal, Apache, PHP5, RedHat, AngularJS\",\"AngularJS\"",
       "Error",
       "https://upb.de/site/drupal/g,\"Joomla, Apache, PHP4, AngularJS\",\"Joomla, PHP4, AngularJS\"",
       "https://upb.de/site/jml,\"PDF generator\",\"PDF generator\"",
       "https://upb.de/,\"Java,Python,PHP5\",\"Java,Python,PHP5\"",
       "https://upb.de/site/,\"Apache, PHP5\",\"Apache\"",
       "https://upb.de/site/Testing,\"PHP5, Apache\",\"\"")

  }

@Test
 fun testSimplifyInputTechnologies() {                                                                                  // Test simple array input data.

 setUpTestDataForStaticCases()

 var index : Int = 0

 while(index < inputURLArrayForStaticCases.size){

  val inputURL = inputURLArrayForStaticCases[index]
  val inputTechnology = inputTechnologiesArrayForStaticCases[index]
  val outputTechnology = outputTechnologiesArrayForStaticCases[index]

  assertEquals(outputTechnology,urlDatabaseSystem.simplifyInputTechnologies(URLDatabaseSystem.Directory(inputURL,inputTechnology)))
  index++

 }

 }

@Test
 fun testProcessCSVFile() {                                                                                             // Test CSV file data.

  setUpTestDataForFileCases()
  urlDatabaseSystem.processCSVFile(inputFilePath,outputFilePath)

 try {                                                                                                                  // Read the output file created

  val inputStream : InputStream = File(outputFilePath).inputStream()
  val reader : BufferedReader = inputStream.bufferedReader()

  var line : String = reader.readLine()
  var index : Int = 0
  while(index < expectedFileOutput.size){
   assertEquals(expectedFileOutput.get(index++),line)                                                                   // compare the data with expected data
   line = reader.readLine()
  }

 }
 catch (ex : Exception){
  ex.printStackTrace()
  assertEquals(true,false)
 }

  urlDatabaseSystem.processCSVFile("","")

 }
}
