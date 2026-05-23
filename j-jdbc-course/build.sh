#!/usr/bin/perl
# j-jdbc 课程构建脚本
use strict;
use warnings;

my $dir = $0;
$dir =~ s/[^\/]+$//;
$dir = '.' unless $dir;
$dir =~ s/\/$//;  # remove trailing slash

sub read_file {
    my ($path) = @_;
    open my $fh, '<:utf8', $path or die "Cannot open $path: $!";
    local $/;
    my $content = <$fh>;
    close $fh;
    return $content;
}

# Read base template
my $base = read_file("$dir/_base.html");

# Read footer
my $footer = read_file("$dir/_footer.html");

# Concatenate all modules
my $modules = '';
for my $f (sort glob("$dir/modules/*.html")) {
    $modules .= read_file($f);
}

# Replace placeholders
$base =~ s/MODULE_CONTENT/$modules/g;
$base =~ s/FOOTER_CONTENT/$footer/g;

# Write output
open my $out, '>:utf8', "$dir/index.html" or die "Cannot write index.html: $!";
print $out $base;
close $out;

print "✅ 课程已生成: $dir/index.html\n";
