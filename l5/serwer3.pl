use HTTP::Daemon;
use HTTP::Status;  
use HTTP::Response;
#use IO::File;

my $d = HTTP::Daemon->new(LocalAddr => 'localhost', LocalPort => 4321,)|| die;
  
print "Please contact me at: <URL:", $d->url, ">\n";

while (my $c = $d->accept) {
    while (my $r = $c->get_request) {
        if($r->uri eq "/") {
		$file_s = "./a.html";
	} else {
		$file_s = "./".$r->uri;
	}
	$c->send_file_response($file_s);
    }
    $c->close;
    undef($c);
}