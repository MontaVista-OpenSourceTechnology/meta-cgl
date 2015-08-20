SUMMARY = "Messaging and membership subsystem for High-Availability Linux"
DESCRIPTION = "heartbeat is a basic high-availability subsystem for Linux-HA. \
It will run scripts at initialization, and when machines go up or down. \
This version will also perform IP address takeover using gratuitous ARPs. \
 \
Heartbeat contains a cluster membership layer, fencing, and local and \
clusterwide resource management functionality. \
 \
When used with Pacemaker, it supports n-node clusters with significant \
capabilities for managing resources and dependencies. \
 \
In addition it continues to support the older release 1 style of \
2-node clustering. \
 \
It implements the following kinds of heartbeats: \
- Serial ports \
- UDP/IP multicast (ethernet, etc) \
- UDP/IP broadcast (ethernet, etc) \
- UDP/IP heartbeats \
- ping heartbeats (for routers, switches, etc.) \
(to be used for breaking ties in 2-node systems) \
"
HOMEPAGE = "http://linux-ha.org/"
SECTION = "System Environment/Daemons"
LICENSE = "GPLv2 & LGPLv2+"
LIC_FILES_CHKSUM = " \
    file://doc/COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b \
    file://doc/COPYING.LGPL;md5=d8045f3b8f929c1cb29a1e3fd737b499 \
"
SRC_URI = " \
    http://hg.linux-ha.org/heartbeat-STABLE_3_0/archive/cceeb47a7d8f.tar.bz2 \
    file://membership-ccm-Makefile.am-fix-warning.patch \
    file://Makefile.am-not-chgrp-in-cross-compile.patch \
    file://configure.in-Error-and-warning-fix.patch \
    file://heartbeat-init.d-heartbeat.in-modify-parameter.patch \
    file://heartbeat-bootstrap-libtool.patch \
    file://heartbeat.service \
"
SRC_URI[md5sum] = "3a98cf97842b8a557728be4fb94c8c5d"
SRC_URI[sha256sum] = "a76ec86cd60428f89536c635a5109cf1e9c7f20283874041a4306960e69952f4"
S = "${WORKDIR}/Heartbeat-3-0-cceeb47a7d8f/"
DEPENDS = "cluster-glue corosync gnutls"
RDEPENDS_${PN} += "python"
inherit autotools-brokensep pkgconfig useradd
EXTRA_OECONF = " \
    STAGING_DIR_TARGET=${STAGING_DIR_TARGET} \
    --disable-fatal-warnings \
    --disable-static \
"
SOURCE1 = "heartbeat/init.d/heartbeat"
CFLAGS_append += "-DGLIB_COMPILATION"

do_configure() {
    ./bootstrap
    isbigendian=`echo ${TUNE_FEATURES} | grep bigendian` || true
    if [ $isbigendian"x" = "x" ] ; then
        CPU_ENDIAN="little"
    else
        CPU_ENDIAN="big"
    fi
    cp -a ./configure ./configure.orig
    if [ ${CPU_ENDIAN} == "little" ]; then
        sed -e "s@CROSS_ENDIAN_INFO@\$as_echo \"#define CONFIG_LITTLE_ENDIAN 1\" >>confdefs.h@g" \
            -e "s@CROSS_LIBDIR@${_LIBDIR}@g" \
        ./configure.orig > ./configure
    else
    sed -e "s@CROSS_ENDIAN_INFO@\$as_echo \"#define CONFIG_BIG_ENDIAN 1\" >>confdefs.h@g" \
        -e "s@CROSS_LIBDIR@${_LIBDIR}@g" \
        ./configure.orig > ./configure
    fi
    oe_runconf ${EXTRA_OECONF}
}
do_compile_prepend() {
    sed -i 's|^hardcode_libdir_flag_spec=.*|hardcode_libdir_flag_spec=""|g' ${HOST_PREFIX}libtool
    sed -i 's|^runpath_var=LD_RUN_PATH|runpath_var=DIE_RPATH_DIE|g' ${HOST_PREFIX}libtool
    make clean
}
do_install_append () {
    sed -i -e 's,/usr/lib/,${libdir}/,' ${WORKDIR}/heartbeat.service
    if ${@base_contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${libexecdir}
        install -m 0755 ${S}/${SOURCE1} ${D}${libexecdir}/heartbeat.init
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/heartbeat.service ${D}${systemd_unitdir}/system/
    fi
}

do_install() {
    sed -i -e 's:/etc/rc.d/init.d/functions:/etc/init.d/functions:g' \
        ${S}/${SOURCE1}
    oe_runmake DESTDIR=${D} install
    # cleanup
    [ -d ${D}/usr/man ] && rm -rf ${D}/usr/man
    [ -d ${D}/usr/share/libtool ] && rm -rf ${D}/usr/share/libtool
    find ${D} -type f -name *.la -exec rm -f {} ';'
    rm -rf ${D}/usr/share/heartbeat/cts
    rm -rf ${D}/usr/share/doc/packages
    rm -rf ${D}/usr/share/heartbeat/ha_propagate
    install -m 0755 ${S}/doc/ha.cf    ${D}/etc/ha.d/ha.cf
    install -m 0600 ${S}/doc/authkeys ${D}/etc/ha.d/authkeys
}

inherit systemd
SYSTEMD_SERVICE_${PN} = "heartbeat.service"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "-r haclient"
USERADD_PARAM_${PN} = " \
    -r -g haclient -d /var/lib/heartbeat/cores/hacluster -M \
    -s /sbin/nologin -c \"heartbeat user\" hacluster \
"
FILES_${PN}-dbg += " \
    ${libdir}/heartbeat/plugins/quorum/.debug \
    ${libdir}/heartbeat/plugins/HBauth/.debug \
    ${libdir}/heartbeat/plugins/tiebreaker/.debug \
    ${libdir}/heartbeat/plugins/HBcomm/.debug \
    ${libdir}/heartbeat/plugins/HBcompress/.debug \
"
FILES_${PN} += " \
    run/heartbeat/ccm \
    run/heartbeat/dopd \
    ${libdir}/tmpfiles.d \
"