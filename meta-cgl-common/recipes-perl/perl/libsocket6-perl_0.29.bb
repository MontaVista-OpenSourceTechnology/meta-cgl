DESCRIPTION = "Perl extensions for IPv6"
HOMEPAGE = "https://metacpan.org/release/Socket6"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://README;beginline=43;md5=b2bfcdf2de2e951c8e4ed544e942d8e1"
PR = "r2"

BBCLASSEXTEND = "native"

CFLAGS += "-D_LARGEFILE_SOURCE -D_LARGEFILE64_SOURCE"
BUILD_CFLAGS += "-D_LARGEFILE_SOURCE -D_LARGEFILE64_SOURCE"

SRC_URI = "https://cpan.metacpan.org/authors/id/U/UM/UMEMOTO/Socket6-${PV}.tar.gz;name=socket6-perl-${PV} \
           file://0001-socket6-perl-fix-configure-error.patch \
"
SRC_URI[socket6-perl-0.29.sha256sum] = "468915fa3a04dcf6574fc957eff495915e24569434970c91ee8e4e1459fc9114"

S = "${WORKDIR}/Socket6-${PV}"

do_configure:prepend () {
	mkdir -p m4
	autoreconf -Wcross --verbose --install --force || oefatal "autoreconf execution failed."
	sed -i 's:\./configure\(.[^-]\):./configure --build=${BUILD_SYS} --host=${HOST_SYS} --target=${TARGET_SYS} --prefix=${prefix} --exec_prefix=${exec_prefix} --bindir=${bindir} --sbindir=${sbindir} --libexecdir=${libexecdir} --datadir=${datadir} --sysconfdir=${sysconfdir} --sharedstatedir=${sharedstatedir} --localstatedir=${localstatedir} --libdir=${libdir} --includedir=${includedir} --infodir=${infodir} --mandir=${mandir}\1:' Makefile.PL
}

inherit autotools-brokensep cpan
