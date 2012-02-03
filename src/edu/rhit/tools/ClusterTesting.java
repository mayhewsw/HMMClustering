package edu.rhit.tools;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class ClusterTesting {
	
	OutputTesting ot;
	HashSet<Cluster> goldClusters;
	HashSet<Cluster> hypClusters;
	Cluster a;
	Cluster b;
	
	@Before
	public void setUp() throws Exception {
		this.ot = new OutputTesting();
		//this.goldClusters = ot.readInClustering(OutputTesting.clusterdir + "clusters.txt");
		//this.hypClusters = ot.readInClustering(OutputTesting.clusterdir + "clusters.txt");
		
		a = new Cluster();
		b = new Cluster();
	
	}

	  
	@After
	public void tearDown() throws Exception { 
	}
	
	@Test
	public void testContains(){
		a.addString(this.s1);
		a.addString(this.s2);
		
		assertTrue(a.contains(this.s1));
		
		a.clear();
	}
	
	@Test
	public void testEquals(){
		a.addString(this.s2);
		a.addString(this.s3);
		a.addString(this.s4);
		a.addString(this.s5);
		
		
		b.addString(this.s2);
		b.addString(this.s3);
		b.addString(this.s4);
		
		assertFalse(a.equals(b));
		
		b.addString(this.s5);
		assertTrue(a.equals(b));
		
		a.clear();
		b.clear();

	}
	
	@Test
	public void testIntersection() {
		a.addString(this.s1);
		b.addString(this.s1);
		
		assertEquals(1, a.getIntersection(b).size());
		
		a.addString(this.s2);
		a.addString(this.s3);
		a.addString(this.s4);
		a.addString(this.s5);
		
		assertEquals(1, a.getIntersection(b).size());
		
		
		b.addString(this.s2);
		
		assertEquals(2, a.getIntersection(b).size());
		
		b.addString(this.s13);
		b.addString(this.s14);
		b.addString(this.s5);
		
		assertEquals(3, a.getIntersection(b).size());
		
		a.clear();
		b.clear();
		
	}
	
	//@Test
	public void testPR(){
		try {
			this.goldClusters = this.ot.readInClustering(OutputTesting.clusterdir + "clusters.txt");
			this.hypClusters = this.ot.readInClustering(OutputTesting.clusterdir + "clusters.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		float [] stats = OutputTesting.calculatePR(goldClusters, hypClusters);
		assertEquals(1.0, stats[0], 0);
		assertEquals(1.0, stats[0], 0);
		
		this.goldClusters.clear();
		this.hypClusters.clear();
		
		a.addString(s21);
		a.addString(s22);
		a.addString(s23);
		
		b.addString(s24);
		b.addString(s25);
		b.addString(s26);
		
		this.goldClusters.add(a);
		this.goldClusters.add(b);
		
		this.hypClusters.add(b);
		
		stats = OutputTesting.calculatePR(goldClusters, hypClusters);
		assertEquals(1.0, stats[0], 0);
		assertEquals(0.5, stats[1], 0);
		
		
		
		
	}
	
	String s1 = "982539	Abe Lincoln	was born on	February 12 , 1809	abe lincoln	be bear on	february 12 , 1809	1	0.96118	http://www.wtisbury.mv.k12.ma.us/projects/bioweb/trevor.htm";
	String s2 = "982544	Abe Lincoln	was born in	1809	abe lincoln	be bear in	1809	1	0.95117	http://xpda.wordpress.com/category/uncategorized/";
	String s3 = "992295	Abraham Lincoln	was born on	February 12 1809	abraham lincoln	be bear on	february 12 1809	2	0.93285	http://yawiki.org/proc/Abraham_Lincoln|http://www.wikipediaondvd.com/nav/art/j/u.html|http://wwww.wikipediaondvd.com/nav/art/j/u.html|http://en.wikivisual.com/index.php/Abraham_Lincoln|http://abadaniran.en.cheapmusicclub.info/en/April 21|http://abraham.lincoln.en.finestfreetune.info/|http://algeria.background.en.kangoshi.info/en/April 13|http://abraham.lincoln.en.linksepeti.info/|http://encycl.opentopia.com/term/Abraham_Lincoln|http://grafika.afganistan.jpg.pl.zakon-rf.info/en/April 7|http://www.book-of-thoth.com/thebook/index.php/Abraham_Lincoln|http://abraham.lincoln.en.chooseoutsourcingcompany.info/|http://history.of.whaling.en.finestfreetune.info/en/20th century";
	String s4 = "992316	Abraham Lincoln	was born in	1809	abraham lincoln	be bear in	1809	1	0.95117	http://www.homefires.com/dvd/february.asp|http://www.allfreeessays.com/essays/Abraham-Lincoln/1338.html";
	String s5 = "992382	Abraham Lincoln	was born on	Feb.12	abraham lincoln	be bear on	feb.12	1	0.91293	http://alifeexamined.blogstream.com/v1/date/200607.html";
	String s6 = "992588	Abraham Lincoln	was born in	February of 1809	abraham lincoln	be bear in	february of 1809	1	0.91618	http://www.bookpage.com/0002bp/nonfiction/lincoln_roundup.html";
	String s7 = "992616	Abraham Lincoln	was born on	12th February 1809	abraham lincoln	be bear on	12th february 1809	1	0.91619	http://lifestyle.iloveindia.com/lounge/abraham-lincoln-2507.html";
	String s8 = "992642	Abraham Lincoln	was born in	February 12 1809	abraham lincoln	be bear in	february 12 1809	1	0.95117	http://www.fcds.org/faculty/amandacrane/First%20Grade%20Presidents/Lincoln.htm";
	String s9 = "7299805	Lincoln	was born in	February	lincoln	be bear in	february	1	0.91018	http://www.usatoday.com/money/workplace/2007-11-17-thanksgiving-day-after_N.htm";
	String s10 = "7300983	Lincoln	was born in	1809	lincoln	be bear in	1809	5	0.95344	http://thomaskennedyart.com/Lincoln-news.htm|http://c-petersonplace.blogspot.com/|http://statecapitols.tigerleaf.com/ta-towers.htm|http://www.statecapitols.tigerleaf.com/ta-towers.htm|http://www.tektonics.org/af/abemyth.html|http://www.thomaskennedyart.com/Lincoln-news.htm|http://kidsblogs.nationalgeographic.com/kidsnews/united-states/|http://www.lsfyw.net/Article/HTML/28326_6.html|http://www.ezhistory.com/lincoln123|http://xroads.virginia.edu/~HYPER/ADAMS_HISTORY/ch06.html";
	String s11 = "7301323	Lincoln	was born on	the 12th of February	lincoln	be bear on	the 12th of february	1	0.96685	http://tigernet.princeton.edu/~ptoniana/history.asp|http://tigernet.princeton.edu/%7Eptoniana/history.asp";

	String s12 = "982543	Abe Lincoln	became President of	the United States of America	abe lincoln	become president of	the united states of america	1	0.93806	http://www.wtisbury.mv.k12.ma.us/projects/bioweb/trevor.htm";
	String s13 = "992254	Abraham Lincoln	became President of	the United States	abraham lincoln	become president of	the united states	5	0.96596	http://www.bellaonline.com/ArticlesP/art22736.asp|http://www.unsv.com/voanews/specialenglish/scripts/2003/07/13/0045/|http://www.csfls.com/bumen/wyz2003/study/tests/s1at2.htm|http://resource.ahedu.cn/statics/tbfd/gzpds/tbfd/g1yy/g1yy06/gdjs1.htm|http://www.studentshelp.de/p/referate/02/3132.htm|http://www.artikel32.com/englisch/1/blacks-in-america.php|http://knowledgenews.net/moxie/americana/battle-hymn-2.shtml";
	String s14 = "992242	Abraham Lincoln	became	a United States President	abraham lincoln	become	a united states president	1	0.90671	http://civics.pwnet.org/3/3.3.4.html";
	String s15 = "7301385	Lincoln	became the new President of	the United States	lincoln	become the new president of	the united states	1	0.92498	http://www.englisch.schule.de/state_of_the_union/group5/project/alincoln.htm|http://www.schule.de/englisch/state_of_the_union/group5/project/alincoln.htm";
	String s16 = "7300041	Lincoln	became the sixteenth president of	the United States	lincoln	become the sixteenth president of	the united states	1	0.91071	http://www.humanillnesses.com/Behavioral-Health-Ob-Sea/Resilience.html";
	String s17 = "7300790	Lincoln	became president of	the United States	lincoln	become president of	the united states	2	0.92498	http://www.school-for-champions.com/history/lincoln_failures.htm|http://www.shortcoming.net/shortcoming/html/20/2006/1010/128.html";
	String s18 = "992403	Abraham Lincoln	did n't go to	law school	abraham lincoln	do not go to	law school	1	0.95757	http://www.lawingenue.blogspot.com/|http://lawingenue.blogspot.com/2009_02_01_archive.html";
	String s19 = "982545	Abe Lincoln	did not go to	law school	abe lincoln	do not go to	law school	1	0.95757	http://zaetsch.blogspot.com/2008_05_11_archive.html";
	String s20 = "7299634	Lincoln	never attended	law school	lincoln	never attend	law school	1	0.92540	http://www.farmersalmanac.com/best_days/a/can_you_name_that_president";

	String s21 = "2626	Abraham Lincoln	was known as	Honest Abe	abraham lincoln	be know as	honest abe	2	0.94723	http://www.educationalsynthesis.org/famamer/ALincoln.html|http://www.cindysthrows.com/product/catalog.cfm/nid/1091";
	String s22 = "982538	Abe Lincoln	was known as	honest Abe	abe lincoln	be know as	honest abe	1	0.93841	http://www.wtisbury.mv.k12.ma.us/projects/bioweb/trevor.htm";
	String s23 = "7301352	Lincoln	was known as	Honest Abe	lincoln	be know as	honest abe	2	0.93596	http://www.abovetopsecret.com/forum/thread421709/pg1|http://www.bronconetwork.com/video/s7oAhIig8I0/Barack-Obama-picks-a-fight-with-Rush-Limbaugh-Memo-to-President-Obama-the-parties-need-to-bicker.html|http://www.farse.info/video-theme/week.html|http://www.pregnancyquestionspro.com/pregnancy-exercise/video-theme/to.html|http://www.bobmodifiedbmw.com/bmw-performance/video-theme/new.html";
	
	String s24 = "992455	Abraham Lincoln	served one term in	the House of Representatives	abraham lincoln	serve one term in	the house of representatives	1	0.91018	http://www.slate.com/id/2182073";
	String s25 = "982540	Abe Lincoln	served one term in	the House	abe lincoln	serve one term in	the house	1	0.91018	http://blip.tv/file/1287551";
	String s26 = "7301016	Lincoln	served only one term in	the U.S. House	lincoln	serve only one term in	the u.s. house	1	0.95825	http://secondeffort.blogspot.com/2009/01/heads-or-tails-72-three-things-learned.html|http://secondeffort.blogspot.com/";
	String s27 = "7299986	Lincoln	served in	the House of Representatives	lincoln	serve in	the house of representatives	1	0.91018	http://www.humanities.gov/news/humanities/2007-01/Americas_Founders.htm|http://www.neh.gov/news/humanities/2007-01/Americas_Founders.htm|http://neh.gov/news/humanities/2007-01/Americas_Founders.htm";
	

}
