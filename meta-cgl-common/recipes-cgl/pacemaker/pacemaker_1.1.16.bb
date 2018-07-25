SUMMARY = "Scalable High-Availability cluster resource manager"
DESCRIPTION = "Pacemaker is an advanced, scalable High-Availability \
cluster resource manager for Linux-HA (Heartbeat) and/or OpenAIS. \
It supports n-node clusters with significant capabilities for \
managing resources and dependencies. \
It will run scripts at initialization, when machines go up or down, \
when related resources fail and can be configured to periodically \
check resource health."
HOMEPAGE = "http://www.clusterlabs.org"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=19a64afd3a35d044a80579d7aafc30ff"

DEPENDS = "corosync libxslt libxml2 gnutls resource-agents libqb python-native"

SRC_URI = "https://github.com/ClusterLabs/${BPN}/archive/Pacemaker-${PV}.zip \
           file://0001-pacemaker-fix-xml-config.patch \
           file://0002-pacemaker-search-header-from-STAGING_INCDIR-to-walka.patch \
           file://0003-pacemaker-fix-header-defs-lookup.patch \
           file://0004-pacemaker-do-not-build-help.patch \
           file://0005-pacemaker-do-not-execute-target-program-while-cross-.patch \
           file://0006-pacemaker-do-not-use-libgnutls-config.patch \
           file://set-OCF_ROOT_DIR-to-libdir-ocf.patch \
           file://volatiles \
           file://tmpfiles \
          "
SRC_URI[md5sum] = "6f60f733823d31acbef4556fb749c457"
SRC_URI[sha256sum] = "2642264b27c584eff5747a2a34c7f2bff13d52741e4e5cc70f14b2b6cd1fe7d5"

inherit autotools-brokensep pkgconfig systemd python-dir useradd

S="${WORKDIR}/pacemaker-Pacemaker-${PV}"

CLEANBROKEN = "1"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd,systemd"
PACKAGECONFIG[libesmtp] = "--with-esmtp=yes,--with-esmtp=no,libesmtp"

EXTRA_OECONF += "STAGING_INCDIR=${STAGING_INCDIR} \
                 --disable-fatal-warnings \
                 --with-ais \
                 --without-heartbeat \
                 --disable-pretty \
                "

do_install_append() {
    install -d ${D}${sysconfdir}/default
    install -d ${D}${sysconfdir}/default/volatiles
    install -m 0644 ${WORKDIR}/volatiles ${D}${sysconfdir}/default/volatiles/06_${BPN}
    install -d ${D}${sysconfdir}/tmpfiles.d
    install -m 0644 ${WORKDIR}/tmpfiles ${D}${sysconfdir}/tmpfiles.d/${BPN}.conf

    # Don't package some files
    find ${D} -name "*.pyo" -exec rm {} \;
    find ${D} -name "*.pyc" -exec rm {} \;
    rm -rf ${D}/${libdir}/service_crm.so

    rm -rf ${D}${localstatedir}/lib/heartbeat
    rm -rf ${D}${localstatedir}/run
}

PACKAGES_prepend = "${PN}-cli-utils ${PN}-libs ${PN}-cluster-libs ${PN}-remote "

FILES_${PN}-cli-utils = "${sbindir}/crm* ${sbindir}/iso8601"
RDEPENDS_${PN}-cli-utils += "libqb bash"
FILES_${PN}-libs = "${libdir}/libcib.so.* \
                    ${libdir}/liblrmd.so.* \
                    ${libdir}/libcrmservice.so.* \
                    ${libdir}/libcrmcommon.so.* \
                    ${libdir}/libpe_status.so.* \
                    ${libdir}/libpe_rules.so.* \
                    ${libdir}/libpengine.so.* \
                    ${libdir}/libstonithd.so.* \
                    ${libdir}/libtransitioner.so.* \
                   "
RDEPENDS_${PN}-libs += "libqb dbus-lib"
FILES_${PN}-cluster-libs = "${libdir}/libcrmcluster.so.*"
RDEPENDS_${PN}-cluster-libs += "libqb"
FILES_${PN}-remote = "${sysconfdir}/init.d/pacemaker_remote \
                      ${sbindir}/pacemaker_remoted \
                      ${libdir}/ocf/resource.d/pacemaker/remote \
                     "
RDEPENDS_${PN}-remote += "libqb bash"
FILES_${PN} += " ${datadir}/snmp                             \
                 ${libdir}/corosync/lcrso/pacemaker.lcrso    \
                 ${libdir}/${PYTHON_DIR}/dist-packages/cts/  \
                 ${libdir}/ocf/resource.d/ \
                 ${libdir}/${PYTHON_DIR}/site-packages \
               "
FILES_${PN}-dbg += "${libdir}/corosync/lcrso/.debug"
RDEPENDS_${PN} = "bash python libqb ${PN}-cli-utils"

SYSTEMD_AUTO_ENABLE = "disable"

SYSTEMD_PACKAGES += "${PN}-remote"
SYSTEMD_SERVICE_${PN} += "pacemaker.service crm_mon.service"
SYSTEMD_SERVICE_${PN}-remote += "pacemaker_remote.service"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "-r -g haclient -s ${base_sbindir}/nologin hacluster"
GROUPADD_PARAM_${PN} = "-r haclient"
