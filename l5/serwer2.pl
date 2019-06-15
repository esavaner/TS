use HTTP::Daemon;
use HTTP::Status;  
use HTTP::Response;
#use IO::File;

my $d = 
HTTP::Daemon->new(LocalAddr => 'localhost', LocalPort => 4321,)|| die;
  
print "Please contact me at: <URL:", $d->url, ">\n";

while (my $c = $d->accept) {
    while (my $r = $c->get_request) {
        $resp = HTTP::Response->new (200, 'Test ok');
	  $resp->content($r->headers_as_string);
	  $c->send_response($resp);
    }
    $c->close;
    undef($c);
}
