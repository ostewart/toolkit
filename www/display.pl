#!/usr/bin/perl -w

# send an (html page containing an) image back to the client based on
# the path info specified in the following way:
# /group/name?size=10
# where the size specification is specified a percentage in the set
# 10, 25, 50, and 100, and is optional, defaulting to 10
# now the script looks for files in DocumentRoot/photo/group/

# future additions include a format specification, database storage
# of pictures and/or comments/other info

# program control:
# stat file pointed to by path_info
# is it a dir or file?
# if file: display_file
# if dir: display_dir

#TODO/BUGS: 
# add height and width tags
# handle NNNxNNN file names
# write a useful 404 handler
# add footer function?
# add file sizes: stat each file and make sure we have
# permission to read it
# find fancier way of listing directories in index
# allow size specification in directories
# allow default size to be specified by cookies
# modularize and develop API to allow easy switching, or
# filename based switching of output format e.g. XML v. HTML
# FIXED - fix for files without 10% version
# FIXED - should be able to handle sizes other than default

#1/1/99 - added file size selection interface
#1/2/99 - added directory indexing
#       - now can use smallest file if default isn't avail.
#       - should be able to handle any size that exists as a file
#       - hopefully the variables in display_file are a little better
#       - added $efaultsize, $suffix
#3/4/99 - added HEAD handling, should send size if possible

use Apache;
use Apache::Constants ':common';
use DBI;
use POSIX;
use strict;

#use constant DB_SOURCE => scalar "dbi:mysql:trailmagic";
use constant DB_SOURCE => scalar "dbi:Pg:dbname=trailmagic";
use constant DB_USER => scalar "nobody";
use constant DB_AUTH => scalar "";

#my $data_source = 'dbi:Pg:dbname=oestewar';
#my $username = 'oestewar';
#my $auth = '';


#my $dbh = DBI->connect( $data_source, $username, $auth) || die $DBI::errstr;

#my $sth = $dbh->prepare(q{});

my $r = Apache->request;
my $subr = $r->lookup_uri("/photo");
my $photopath = $subr->filename;

# stat the file pointed to by the path_info (with nothing added)
# if it's a directory and is readable and searchable, call
# display_dir with the request structure, otherwise call
# display_file with the request structure
stat( $photopath . $r->path_info );
if ( -d _ and -r _ and -x _ ) { &display_dir($r, $photopath); }
else { &display_file($r, $photopath); }


# display_dir:
# display a directory index showing the 10% views of each picture
# in the directory, takes an Apache request as an argument

sub display_dir {
    my ($r, $photopath) = @_;

    # declare variables
    my ($dirname, $dirurl, $displayurl, @files, %filenames);

    # set the directory name and urls
    $dirname = $photopath . $r->path_info;
    $dirurl = "/photo" . $r->path_info;
    $displayurl = "/display.pl" . $r->path_info;

    # make sure the paths end with a '/'
    if ( $r->path_info !~ /[\/]$/ ) {
	$dirname = $dirname . "/";
	$dirurl = $dirurl . "/";
	$displayurl = $displayurl . "/";
    }

    # open the directory or return 404
    opendir( IMAGEDIR, $dirname ) || do {
	$r->status( 404 );
	
	$r->custom_response( NOT_FOUND, qq{
	    <html>
	    <head>
	    <title>File not found.</title>
	    </head>
       	    <body>File $dirname not found.</body>
       	    </html>
	});

	return;
    };

    # set the content-type, send the header
    # the document
    $r->content_type( "text/html" );
    $r->send_http_header;

    # if the request is a HEAD, stop here
    if ( $r->method =~ /HEAD/ ) { exit; }

    # start sending the document
    print <<END;
<!doctype html public "-//W3C/DTD HTML 4.0//EN">
<html>
<head>
<title>Directory Index: 
END
    print $r->path_info;
    print <<END;
</title>
<link rel="StyleSheet" href="/trailmagic.css" type="text/css">
<style type="text/css">
    IMG.display { float: center }
</style>
</head>

<body>
<h1>Directory index of
END
    print " ", $r->path_info, "</h1>\n\n";


    # for each file in the directory, display the 10% size,
    # or the smallest size available

    # get a list of the files in the directory
    @files = readdir IMAGEDIR;

    # make a hash by filename base of hashes of sizes
    # available, where each entry holds the filename
    foreach my $file (@files) {
	if ($file =~ /([^\/]*[^\/\d]+)(\d+)[.]jpeg/) {
	    $filenames{$1}{$2} = $&;
	} elsif ( -d "$dirname$file" and $file !~ /^[.]/ ) {
	    print( "DIRECTORY: <a href=\"$displayurl$file\">",
		   "$displayurl$file</a><br>\n");
	}
#    print "<hr>$dirname$file<br>";
    }
#    print join "<br>", @files;

    my $count = 1;

    # for each file available, generate an entry on the page
    foreach my $base (sort keys %filenames) {
	print "<a href=\"$displayurl$base\">\n<img";

	print " src=\"$dirurl";

	if ( exists $filenames{$base}{10} ) { 
	    print $filenames{$base}{10};
	} else {
	    my ($index) = sort { $a <=> $b } keys %{$filenames{$base}};
	    print $filenames{$base}{$index};
	}
	print( "\">\n</a>\n\n" );
	print "\n<br>\n";

	$count++;
    }

    # print the end of the HTML
    print <<END;
<br><hr>
    <address>All contents Copyright 1999
      <a href="mailto:oliver\@trailmagic.com">Oliver Stewart</a>.
	  All rights reserved.</address>
</body>
</html>
END
}



# display_file:
# present an image with the image sizes available under the
# image, takes an Apache request as an argument

sub display_file {
    my ($r, $photopath) = @_;
    my %args = $r->args;
    my $size = $args{size};
    my $defaultsize = 10;
    my $suffix = ".jpeg";

    my (@files, @sizes, $filename, $filebasename,
	$filebasepath, $filebaseurl, $fileurl, $subr,
	$filepathname);
    
    # $filepathname - full file path + filename
    # $filebasename - filename without size, suffix, or path
    # $filebasepath - filebasename + full path
    # $filename - full file name without path
    # $filebaseurl - full file url + name without size + suffix
    # $fileurl - fileurlbase + size + suffix

#    $filebasepath = $r->document_root . "/photo" . $r->path_info;
    $filebasepath = $photopath . $r->path_info;
    

    $filebaseurl = "/photo" . $r->path_info;

    $filebasepath =~ /[^\/]*[^\/\d]+$/;
    $filebasename = $&;

    # if no size is given, set it to ten
    if (!defined $size) { $size = $defaultsize };

    # read the directory to find the sizes available
    # NOTE: the filenames must be of form:
    # filenameXXX.jpeg
    # where XXX is the percentage of full size

    # open the directory
    $filebasepath =~ /(.*)\/[^\/]+$/;
    opendir( IMAGEDIR, $1 ) || print "IMAGEDIR not opened!\n";
    
    @files = grep /$filebasename\d+[.]jpeg/, readdir IMAGEDIR;
    
    foreach my $file (@files) {
	$file =~ /(\d+)[.]jpeg$/;
	push @sizes, $1;
    }
    
    closedir( IMAGEDIR );

    # find out the size:
    # first look in the available sizes
    if ( (grep /^$size$/, @sizes) > 0 ) {;}
    else { ($size) = sort @sizes }

    $filename = $filebasename . $size . $suffix;
    $filepathname = $filebasepath . $size . $suffix;
    $fileurl = $filebaseurl . $size . $suffix;
    
    # if the size is in 10,25,50,100, set the filename with that
    # size. If another (invalid) size is given, set the size to 10
#    if ( $size =~ /10$/ || $size =~ /25$/
#	 || $size =~ /50$/ || $size =~ /100$/ ) {
#	$filename = $photopath . $r->path_info
#	    . $size . ".jpeg";
#	$fileurlbase = "/photo" . $r->path_info;
#	$fileurl = $fileurlbase . $size . ".jpeg";
#    } else {
#	$filename = $r->document_root . "/photo" . $r->path_info . "10.jpeg";
#	$fileurlbase = "/photo" . $r->path_info;
#	$fileurl = $fileurlbase . "10.jpeg";
#    }
    
    
    # try to open the image file, if we can't, it's a 404
    open(IMAGE, $filepathname) || do {
	$r->status( 404 );
	
	$r->custom_response( NOT_FOUND, qq{
	    <html>
	    <head>
	    <title>File not found.</title>
	    </head>
       	    <body>File $filepathname not found.</body>
       	    </html>
});

	return;
    };
    
    
    # set the content-type and send the header
    #$r->content_type("image/jpeg");
    $r->content_type("text/html");
    $r->send_http_header;
    #$r->send_fd(IMAGE);
    close(IMAGE);

    # if this is a HEAD request, we're done
    if ( $r->method =~ /HEAD/ ) { exit; }
    
    # write the HTML
    print <<END;
<!doctype html public "-//W3C/DTD HTML 4.0//EN">
<html>
<head>
<title>$fileurl</title>
<link rel="StyleSheet" href="/trailmagic.css" type="text/css">
<style type="text/css">
    IMG.display { float: center }
</style>
</head>

<body>
<center>
<img class="display" src="$fileurl">
<br>
END

    # add the file size selector
    print "|", map( join ("", ($size == $_) ? "<b>"
			  : ("<a href=\"/display.pl", $r->path_info,
			     "?size=", scalar($_), "\">"),
			  &round(3894/(100/$_)), "x",
			  &round(2592/(100/$_)),
			  ($size == $_) ? "</b>" : "</a>", "|"),
		    sort { $a <=> $b } @sizes ), "\n";
    
    # query the db for description, title, and copyright string
    my $dbh;
  DB:
    {
        # XXX: don't bother connecting to database
        last DB;
	$dbh = DBI->connect( DB_SOURCE, DB_USER, DB_AUTH ) or do {
		print STDERR "display.pl: Unable to connect: $DBI::errstr\n";
		last DB;
	    };
	
	my $query = qq{
	    SELECT title, description, copyright
	    FROM photo
	    WHERE filename_base = @{[$dbh->quote($filebasename)]}
	};
    
	my @photo_row = $dbh->selectrow_array( $query ) or do {
	    print STDERR "display.pl: Select failed: $DBI::errstr\n";
	    last DB;
	};
	
	# print the title
	if ( defined $photo_row[0] ) {
	    print "<em>$photo_row[0]</em><br>\n";
	    print "\n<br>\n";
	}

	# print the description and copyright
	if ( defined $photo_row[1] ) {
	    print "$photo_row[1]<br>\n";
	}
	if ( defined $photo_row[2] ) {
	    print "<br><em>Photo $photo_row[2]</em><br>\n";
	}
    }
    if ( defined $dbh ) { $dbh->disconnect; }

    print <<END;
</center>
<br><hr>
    <address>All contents Copyright 1999
      <a href="mailto:oliver\@trailmagic.com">Oliver Stewart</a>.
	  All rights reserved.</address>
</body>
</html>
END
}


# round
# round to an integer using normal rules, i.e. >= .5 round up,
# otherwise round down
sub round {
    my ($value) = @_;
    my ($fractional, $integral) = POSIX::modf($value);
    if ($fractional >= .5) { return ($value - $fractional + 1); }
    else { return ($value - $fractional); }
}
