SUMMARY = "An implementation of key management system for IPsec"
DESCRIPTION = "The Racoon2 project is a joint effort which provides an \
implementation of key management system for IPsec. The implementation is \
called Racoon2, a successor of Racoon, which was developed by the KAME \
project. It supports IKEv1, IKEv2, and KINK protocols. It works on \
FreeBSD, NetBSD, Linux, and Mac OS X."

HOMEPAGE = "http://ftp.racoon2.wide.ad.jp/pub/racoon2/"

DEPENDS = "${@bb.utils.contains('DISTRO_FEATURES', 'krb5', 'krb5', '', d)} libpcap openssl bison flex-native util-linux bison-native"
RDEPENDS:${PN} += "perl"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=99a60756441098855c538fe86f859afe"

SRC_URI = "git://github.com/zoulasc/racoon2;branch=master;protocol=https \
           file://0001-Add-DESTDIR-to-install-commands.patch \
           file://0002-Enable-turning-of-kinkd-and-iked.patch \
           file://0003-Replace-perl_bindir-with-usr-bin-env-perl.patch \
           file://0004-racoon2-disable-hard-limit-timer.patch \
           file://0005-racoon2-fix-rekeying-reply.patch \
           file://0006-racoon2-fix-sadb_msg_seq-collision.patch \
           file://0007-racoon2-fwrite-return-value.patch \
           file://0008-racoon2-iked-needs-libcrypto.patch \
           file://0009-racoon2-iked-initscript.patch \
           file://0010-racoon2-kinkd-initscript.patch \
           file://0011-racoon2-spmd-initscript.patch \
           file://0012-racoon2-remove-deprecated-do-clause.patch \
           file://0013-racoon2-fix-hardcoded-sysconfdir.patch \
           file://volatiles.99_racoon2 \
           file://iked.service \
           file://spmd.service \
          "

SRCREV="7b68950328454b0e91ba24698c10c4a790705cc1"
S= "${WORKDIR}/git" 
SRC_URI[md5sum] = "2fa33abff1ccd6fc22876a23db77aaa8"
SRC_URI[sha256sum] = "f23773e4d97cec823ec634085b5e60a7884a13467ff1bffc17daac14d02f9caa"

inherit autotools-brokensep update-rc.d systemd

EXTRA_OECONF += "--sysconfdir=${sysconfdir}/${BPN} \
                 --disable-pedant \
                 --enable-pcap=yes \
                 --enable-iked=yes \
                 ${@bb.utils.contains('DISTRO_FEATURES', 'krb5', '--enable-kinkd', '--disable-kinkd', d)} \
                 ${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', '--enable-ipv6', '--disable-ipv6', d)} \
                 --with-openssl-libdir=${STAGING_DIR_TARGET}${prefix} \
                 --with-kernel-build-dir=${STAGING_INCDIR}"
CLEANBROKEN = "1"

do_configure:prepend () {
    mkdir -p lib/m4 spmd/m4 iked/m4 kinkd/m4
}

do_install:append() {
    install -d -m 0755 ${D}${sysconfdir}/init.d/
    cp -rfa ${D}${sysconfdir}/${BPN}/init.d/* ${D}${sysconfdir}/init.d/

    rmdir ${D}${localstatedir}/run/racoon2 ${D}${localstatedir}/run ${D}${localstatedir}
    install -d ${D}${sysconfdir}/default/volatiles
    install -m 0700 ${WORKDIR}/volatiles.99_racoon2 ${D}${sysconfdir}/default/volatiles/99_racoon2
    if ${@bb.utils.contains('DISTRO_FEATURES', 'krb5', 'false', 'true', d)}; then
        rm -f ${D}${sysconfdir}/init.d/kinkd ${D}${sysconfdir}/${BPN}/init.d/kinkd \
        ${D}${sysconfdir}/${BPN}/transport_kink.conf.sample ${D}${sysconfdir}/${BPN}/tunnel_kink.conf.sample
    fi

    # Install systemd service files
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/iked.service ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/spmd.service ${D}${systemd_system_unitdir}
    sed -i -e 's#@SBINDIR@#${sbindir}#g' -e 's,@BASE_BINDIR@,${base_bindir},g' \
        ${D}${systemd_system_unitdir}/*.service

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d /var/run/racoon2 0700 root root -" \
        > ${D}${sysconfdir}/tmpfiles.d/${BPN}.conf
    fi
}

INITSCRIPT_PACKAGES = "${PN} ${PN}-iked ${PN}-${@bb.utils.contains('DISTRO_FEATURES', 'krb5', 'kinkd', '', d)}"
INITSCRIPT_NAME:${PN} = "spmd"
INITSCRIPT_PARAMS:${PN} = "remove"
INITSCRIPT_NAME:${PN}-iked = "iked"
INITSCRIPT_PARAMS:${PN}-iked = "remove"
INITSCRIPT_NAME:${PN}-kinkd = "kinkd"
INITSCRIPT_PARAMS:${PN}-kinkd= "remove"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "spmd.service iked.service"
SYSTEMD_AUTO_ENABLE = "disable"


pkg_postinst:${PN} () {
    if [ -z "$D" ]; then
        if [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
            ${sysconfdir}/init.d/populate-volatile.sh update
        fi
    fi
}

