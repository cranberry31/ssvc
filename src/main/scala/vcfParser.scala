package svComp

import utils._

trait Breakend {
	val chr: String
	val end: Long
	val orientation: String
}

case class EndPos(chro: String, pos: Long, info: Array[String]) extends Breakend {

	val endTag = info.find(_.startsWith("END")).getOrElse("=0").split("=").last.toLong
	val len = info.find(_.startsWith("SVLEN")).getOrElse("=0").split("=").last.toLong

	val chr = chro
	val end = if (len != 0) pos + abs(len) else endTag
	val orientation = ""
}

case class BND(alt: String) extends Breakend {

	val pat = "X|Y|[0-9]+".r
	val Array(endChr,endPos) = pat.findAllIn(alt).toArray 
	
	val chr = "chr"+endChr
	val end = endPos.toLong
	
	val orientation = alt.collect { case c if !c.isLetterOrDigit => c }
	def isForward = orientation == "[:["
	def isReverse = orientation == "]:]"
}

object VCFParser {
	
	def extractRegions(chr: String, svType: String, maxbp: Long, vcfFilename: String, filesMap: Map[String, String]): Array[Region] = {
		
		val data = io.Source.fromFile(vcfFilename).getLines.dropWhile(_.startsWith("#"))
		
		val chrLines = data.filter(_.startsWith(chr + "\t"))
		
		val svLines = chrLines.filter(_.contains("SVTYPE="+svType.toUpper))

		val regions = 
			for { line <- svLines
				  items = line.split("\t")
				  info = items.last.split(";")
				} 
			yield {
				val pos = items(1).toLong

				val b = if (svType == "BND") BND(items(4)) else EndPos(chr, pos, info)

				Region(a=pos, b=b, fromOverlap=false, chr=chr, svType=svType.toUpper, maxbp=maxbp, bpDist = 0, origLine = line, files=Set(filesMap(vcfFilename)+":"+chr+":"+pos))
			}

		regions.toArray
	}

}
