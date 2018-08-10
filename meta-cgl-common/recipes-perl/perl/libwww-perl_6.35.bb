DESCRIPTION = "libwww-perl provides a simple and consistent API to the World Wide Web"
HOMEPAGE = "https://metacpan.org/release/libwww-perl"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=225d44a95fa3addb1da6d91187ab189f"
DEPENDS = "liburi-perl-native libhtml-parser-perl-native libhtml-tagset-perl-native"
RDEPENDS_${PN} += " \
	libhtml-parser-perl \
	libhtml-tagset-perl \
	liburi-perl \
	perl-module-digest-md5 \
	perl-module-net-ftp \
	"
BBCLASSEXTEND = "native"

PR = "r2"

SRC_URI = "https://cpan.metacpan.org/authors/id/E/ET/ETHER/libwww-perl-${PV}.tar.gz;name=libwww-perl-${PV}"
SRC_URI[libwww-perl-6.35.md5sum] = "19cd0f55f61359c973caa9301ec405bd"
SRC_URI[libwww-perl-6.35.sha256sum] = "dda2578d7b32152c4afce834761a61d117de286c705a9f7972c7ac6032ca5953"

S = "${WORKDIR}/libwww-perl-${PV}"

inherit cpan
