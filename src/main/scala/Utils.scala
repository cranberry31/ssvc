package ssvc

import scala.language.reflectiveCalls

import org.rogach.scallop._


package object utils {

	@inline def max(x: Long, y: Long) = if (x < y) y else x

	@inline def min(x: Long, y: Long) = if (x < y) x else y	

	@inline def abs(x: Long) = if (x < 0) -x else x

	implicit class OpsStr(val str: String) extends AnyVal {
		def toUpper = str.map(_.toUpper)
	}

	def getOpts(args: Array[String]) = new ScallopConf(args) {
	    
	    banner("""ssvc - Simple Structural Variant Comparator v0.1""")

	    val vcf = opt[List[String]]("vcf", required = true, noshort = true, descr = "VCF files and folders.")
	    val chr = opt[String]("chr", required = true, noshort = true, descr = "Chromosome of interest.")
	    val svType = opt[String]("svType", required = true, noshort = true, descr = "Structural variant type.")
	    val maxbp = opt[Long]("maxbp", default = Some(0), required = false, noshort = true, descr = "Max base pair reads two compared breakpoints may differ by.")

	    val overlap = toggle("overlap", prefix = "no-", default = Some(true), noshort = true, descrYes = "Infer overlapping regions.", descrNo = "Infer non overlapping regions.")    	    

	    val help = opt[Boolean]("help", noshort = true, descr = "Show this message.")
	}

}
