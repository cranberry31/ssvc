package ssvc

case class Region(a: Long, b: Breakend, fromOverlap: Boolean = false, chr: String = "", svType: String = "", maxbp: Long = 0, bpDist: Long = 0, origLine: String, files: Set[String] = Set.empty) {
	import utils._
	
	@inline def dist(x: Long, y: Long) = abs(x-y)

	@inline def isOverlap(i: Region) = chr == b.chr && b.orientation == i.b.orientation && dist(a, i.a) <= maxbp && dist(b.end, i.b.end) <= maxbp

	@inline def maxDist(i: Region) = max(dist(a, i.a), dist(b.end, i.b.end))
	
	def overlap(i: Region) = this.copy(fromOverlap = true, bpDist = maxDist(i), files = files ++ i.files)

	val svLen = if (svType == "DEL") a-b.end else b.end-a

	val overlapType = if (fromOverlap) "FROMOVERLAP" else "NOOVERLAP"

	override def toString() = s"$origLine;$overlapType;MAXBPDIST=$bpDist;FILES="+files.mkString(",")
}


object Region {

	implicit def ordering[A <: Region]: Ordering[A] = Ordering.by( i => (i.a,i.b.end) )

	def intersect(regions: Array[Region]) = {
		
		val iRegions = regions.sorted

		if (iRegions.size < 2) {
			iRegions
		}
		else {
			iRegions.drop(1).foldLeft(Array(iRegions.head)) { (acc,r) => 
				if (acc.last isOverlap r) 
					acc.dropRight(1) :+ (acc.last overlap r)
				else 
					acc :+ r
			}
		}
	}

  def fromOverlap(regions: Array[Region]) = intersect(regions).filter(_.fromOverlap)
  def nonOverlap(regions: Array[Region]) = intersect(regions).filterNot(_.fromOverlap)
}
