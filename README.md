This is a utility to create Twitter Bootstrap styled nav bar with drop down menus from the output of Lift's Menu.builder. 
Any menu item that is defined with submenus will automatically turned into the drop-down menu.

You can see a running example of the code here <TBC>

To use the utility in your own project:

1. download and build the TBUtils library:

        git clone https://github.com/dph01/lift-TBUtils
        cd liftTBUtils
        ./sbt publish-local`

2. In the project in which you want to use the library, add the following to the dependencies in the project's build.sbt:

        "com.damianhelme" %% "tbutils" % "0.1.0" % "compile"

3. In Boot.scala, define your menu sitemap with something like:

        val entries = List(Menu("Home") / "index",
          Menu("Page 1") / "page1",
          Menu("Page 2") / "page2",
          Menu("Page 3") / "#"  >> PlaceHolder submenus (
            Menu("Page 3a") / "page3a" ,  
            Menu("Page 3b") / "page3b" ,
            Menu("Page 3c") / "page3c"))
        
        def sitemap = SiteMap(entries: _*)
        LiftRules.setSiteMap(sitemap)

    Every menu entry with a submenu will the rendered as a menu drop-down. The 'PlaceHolder' is optional, but it's clearer to
    other developers that you intend this entry to be the root of a drop-down if you include it.

4. Download the Twitter Bootstrap libraries from http://twitter.github.com/bootstrap/assets/bootstrap.zip and copy into 
a subdirectory of /src/main/webapp (e.g. /src/main/webapp/bootstrap/2.0.0)

5. Include the Bootstrap files in your html:

        <link rel="stylesheet" type="text/css" href="/bootstrap/1.4.0/bootstrap.css">
        <script id="bootstrap-dropdown" src="/bootstrap/1.4.0/js/bootstrap-dropdown.js" type="text/javascript"></script>
        
5. Make sure jquery 1.7.0 or later is included in your html. An easy way to do this would be to copy the jquery file into 
a subdirectory of /sr/main/webpp (e.g. /src/main/webmapp/jquery) and include a line such as the following in your html:

        <script id="jquery" src="/jquery/jquery-1.7.1.js" type="text/javascript"></script>

4. In your html, wrap your call to Menu.builder with call to TBNav.menuToTBNav: 

        <div class="topbar">
         <div class="navbar-inner">
          <div class="fill">
            <div class="container">
              <a class="brand" href="/">My App</a> 
              <span class="lift:TBNav.menuToTBNav?eager_eval=true"> 
                <span data-lift="lift:Menu.builder?top:class=nav;li_item:class=active;linkToSelf=true;expandAll=true"></span>
              </span>
            </div>
          </div>
        </div>
    The classes topbar, navbar-inner, fill and container are Bootstrap class for formatting the navigation bar.



