DESCRIPTION = "MailTools is a set of Perl modules related to mail applications"
HOMEPAGE = "http://search.cpan.org/dist/MailTools/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://lib/Mail/Mailer.pod;beginline=144;md5=641bd171b1aaabba1fc83ac0a98a2d30"
DEPENDS = " \
	libtest-pod-perl-native \
	libtimedate-perl-native \
	"	
RDEPENDS_${PN} += " \
	libtest-pod-perl \
	libtimedate-perl \
	perl-module-io-handle \
	perl-module-net-smtp \
	perl-module-test-more \
	"
BBCLASSEXTEND = "native"
PR = "r2"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/M/MA/MARKOV/MailTools-${PV}.tar.gz"
SRC_URI[md5sum] = "53e9d35256c3fd7cef0e4a24b15e9512"
SRC_URI[sha256sum] = "f55606f7a9cc342ee9d5f996e2b6a4c0047e2ee47cd88c3250ecf0d0c5fb3196"

S = "${WORKDIR}/MailTools-${PV}"

inherit cpan
