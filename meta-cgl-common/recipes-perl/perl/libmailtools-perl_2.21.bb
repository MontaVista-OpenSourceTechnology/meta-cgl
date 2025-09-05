DESCRIPTION = "MailTools is a set of Perl modules related to mail applications"
HOMEPAGE = "http://search.cpan.org/dist/MailTools/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://lib/Mail/Mailer.pod;beginline=150;md5=641bd171b1aaabba1fc83ac0a98a2d30"
DEPENDS = " \
	libtest-pod-perl-native \
	libtimedate-perl-native \
	"	
RDEPENDS:${PN} += " \
	libtest-pod-perl \
	libtimedate-perl \
	perl-module-io-handle \
	perl-module-net-smtp \
	perl-module-test-more \
	"
BBCLASSEXTEND = "native"
PR = "r2"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/M/MA/MARKOV/MailTools-${PV}.tar.gz"
SRC_URI[sha256sum] = "4ad9bd6826b6f03a2727332466b1b7d29890c8d99a32b4b3b0a8d926ee1a44cb"

S = "${UNPACKDIR}/MailTools-${PV}"

inherit cpan
