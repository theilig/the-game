use strict;
open(INPUT, "<services/compose/docker-compose.yml") or die "can't find compose file";
my @lines = <INPUT>;
close INPUT;

open(OUTPUT, ">services/compose/docker-compose.yml");
my $version = '';
foreach my $line (@lines) {
   if ($line =~ /thegame.*(\d+)\.(\d+)/) {
       my $old_version = $1 . '.' . $2;
       $version = $1 . '.' . ($2 + 1);
       $line =~ s/$old_version/$version/;
    } 
    print OUTPUT $line;
}
if (!$version) {
    die "couldn't find version";
}
print $version;

