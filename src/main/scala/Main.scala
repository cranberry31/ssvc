package ssvc

import java.io.File

import scala.language.reflectiveCalls

import org.rogach.scallop._

import utils._


object Main extends App {

  val opts = getOpts(args)

  val vcfFiles = opts.vcf().toArray.flatMap { path =>
  	val file = new File(path)
  	if (file.isDirectory) file.listFiles.filter(_.getAbsolutePath.endsWith("vcf")).map(_.getAbsolutePath)
  	else Array(file.getAbsolutePath)
  }  

  println(s"""##INFO=<ID=MAXBPDIST,Type=Integer,Description="Max bp difference between compared regions start or end breakpoints, 0 means identical breakpoints.">""")
	vcfFiles.zipWithIndex.foreach { case (vcf,i) => println(s"""##INFO=<ID=FILE$i,Type=String,Description="$vcf">""") }

	val filesMap = vcfFiles.zipWithIndex.map { case (vcf,i) => (vcf,"FILE"+i) }.toMap

	val regions = vcfFiles.par.flatMap ( vcf => VCFParser.extractRegions(opts.chr(), opts.svType().toUpper, opts.maxbp(), vcf, filesMap) ).toArray
	
  if (opts.overlap()) Region.fromOverlap(regions) foreach println
  else Region.nonOverlap(regions) foreach println

}
