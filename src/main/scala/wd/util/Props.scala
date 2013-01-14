package wd.util

import scala.util.Properties
import java.io.FileReader

class Props(filename: String) {
	val props = new java.util.Properties
	props.load(new FileReader(filename))
	
	def apply(key: String): String = props.getProperty(key)
}