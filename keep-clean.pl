#!perl -w
use strict;
use Fatal qw(open close);

my @files = split "\n", `find plugins -name \*.java`;

my @files_with_tabs_elsewhere;
my @files_with_ws_at_end;

for my $file (@files) {

    next if $file !~ /net\.bioclipse\./;   # not our codebase

    open my $FH, '<', $file;

    my @output;

    my $tabs_at_beginning = 0;
    my $tabs_elsewhere    = 0;
    my $empty_line_ws     = 0;
    my $ws_at_end         = 0;
    for my $line (<$FH>) {
#        if ($line =~ /^\t/) {
#            ++$tabs_at_beginning;
#            $line =~ s/^(\s*?)\t/$1        / while $line =~ /^(\s*?)\t/;
#        }
        if ($line =~ /\t/) {
            ++$tabs_elsewhere;
            $line =~ s/\t/        /g;   # technically, this is wrong
        }
        if ($line =~ /^\s+$/) {
            ++$empty_line_ws;
            $line = '';
        }
#        if ($line =~ /\s$/) {
#            ++$ws_at_end;
#            $line =~ s/\s+$//;
#        }

        push @output, $line;
    }
    close $FH;

    if ($tabs_elsewhere) {
        push @files_with_tabs_elsewhere, $file;
    }
    elsif ($ws_at_end) {
        push @files_with_ws_at_end, $file;
    }

    if ($tabs_at_beginning || $tabs_elsewhere || $empty_line_ws
        || $ws_at_end) {

        open my $OFH, '>', $file;
        print {$OFH} join "", @output;
    }
}

print `svn diff $_` for @files_with_tabs_elsewhere,
                        @files_with_ws_at_end;
