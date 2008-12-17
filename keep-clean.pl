#!perl -w
use strict;
use Fatal qw(open close);

my @files = split "\n", `find plugins -name \*.java`;

for my $file (@files) {

    next if $file !~ /net\.bioclipse\./;   # not our codebase

    open my $FH, '<', $file;

    my @output;

    my $tabs_at_beginning = 0;
    my $tabs_elsewhere    = 0;
    my $empty_line_ws     = 0;
    my $ws_at_end         = 0;
    for my $line (<$FH>) {
        if ($line =~ /^\t/) {
            ++$tabs_at_beginning;
            $line =~ s/^(\s*?)\t/$1        / while $line =~ /^(\s*?)\t/;
        }
        if ($line =~ /\t/) {
            ++$tabs_elsewhere;
            $line =~ s/\t/        /g;   # technically, this is wrong
        }
        if ($line =~ /^\s+$/) {
            ++$empty_line_ws;
            $line = '';
        }
        if ($line =~ /\s$/) {
            ++$ws_at_end;
            $line =~ s/\s+$//;
        }

        push @output, $line;
    }
    close $FH;

    if ($tabs_at_beginning || $tabs_elsewhere || $empty_line_ws
        || $ws_at_end) {

        print $file, "\n===\n";
        print "contains tabs at start-of-line\n" if $tabs_at_beginning;
        print "contains tabs elsewhere\n" if $tabs_elsewhere;
        print "has empty lines with whitespace\n" if $empty_line_ws;
        print "has line-ending whitespace\n" if $ws_at_end;
        print "\n";

        open my $OFH, '>', $file;
        print {$OFH} join "\n", @output;
    }
}
