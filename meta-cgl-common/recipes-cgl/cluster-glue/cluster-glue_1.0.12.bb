DESCRIPTION = "Cluster Glue is a set of libraries, tools and utilities suitable for \
the Heartbeat/Pacemaker cluster stack. In essence, Glue is everything that \
is not the cluster messaging layer (Heartbeat), nor the cluster resource manager \
(Pacemaker), nor a Resource Agent."
HOMEPAGE = "http://clusterlabs.org/"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=b70d30a00a451e19d7449d7465d02601 \
                    file://COPYING.LIB;md5=c386bfabdebabbdc1f28e9fde4f4df6d \
"

DEPENDS = "libxml2 libtool glib-2.0 bzip2 util-linux net-snmp openhpi"

SRC_URI = " \
    git://github.com/ClusterLabs/${BPN}.git;branch=master;protocol=https \
    file://0001-don-t-compile-doc-and-Error-Fix.patch \
    file://0001-ribcl.py.in-Warning-Fix.patch \
    file://0001-Update-for-python3.patch \
    file://volatiles \
    file://tmpfiles \
"
SRC_URI:append_libc-uclibc = " file://kill-stack-protector.patch"

SRCREV = "fd5a3befacd23d056a72cacd2b8ad6bba498e56b"

inherit autotools useradd pkgconfig systemd multilib_script multilib_header

SYSTEMD_SERVICE:${PN} = "logd.service"
SYSTEMD_AUTO_ENABLE = "disable"

HA_USER = "hacluster"
HA_GROUP = "haclient"

S = "${WORKDIR}/git"
PV = "1.0.12+git${SRCPV}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_system_unitdir},--without-systemdsystemunitdir,systemd"

EXTRA_OECONF = "--with-daemon-user=${HA_USER} \
                --with-daemon-group=${HA_GROUP} \
                --disable-fatal-warnings \
                --with-ocf-root=${libdir}/ocf \
               "

CACHED_CONFIGUREVARS="ac_cv_path_XML2CONFIG=0 \
                      ac_cv_path_SSH=ssh \
"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--home-dir=${localstatedir}/lib/heartbeat/cores/${HA_USER} \
                       -g ${HA_GROUP} -r -s ${sbindir}/nologin -c 'cluster user' ${HA_USER} \
                      "
GROUPADD_PARAM:${PN} = "-r ${HA_GROUP}"

MULTILIB_SCRIPTS = "${PN}:${sbindir}/cibsecret"

do_configure:prepend() {
    ln -sf ${PKG_CONFIG_SYSROOT_DIR}/usr/include/libxml2/libxml ${PKG_CONFIG_SYSROOT_DIR}/usr/include/libxml
}

do_install:append() {
	install -d ${D}${sysconfdir}/default/volatiles
	install -m 0644 ${WORKDIR}/volatiles ${D}${sysconfdir}/default/volatiles/04_cluster-glue
	install -d ${D}${sysconfdir}/tmpfiles.d
	install -m 0644 ${WORKDIR}/tmpfiles ${D}${sysconfdir}/tmpfiles.d/${PN}.conf

    oe_multilib_header heartbeat/glue_config.h
}

pkg_postinst:${PN} () {
	if [ -z "$D" ]; then
		if type systemd-tmpfiles >/dev/null; then
			systemd-tmpfiles --create
		elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
			${sysconfdir}/init.d/populate-volatile.sh update
		fi
	fi
}

NOAUTOPACKAGEDEBUG = "1"
PACKAGES =+ "\
	 ${PN}-plugin-test \
	 ${PN}-plugin-test-dbg \
	 ${PN}-plugin-test-staticdev \
	 ${PN}-plugin-stonith2 \
	 ${PN}-plugin-stonith2-dbg \
	 ${PN}-plugin-stonith2-staticdev \
	 ${PN}-plugin-stonith2-ribcl \
	 ${PN}-plugin-stonith-external \
	 ${PN}-plugin-raexec \
	 ${PN}-plugin-raexec-dbg \
	 ${PN}-plugin-raexec-staticdev \
	 ${PN}-plugin-interfacemgr \
	 ${PN}-plugin-interfacemgr-dbg \
	 ${PN}-plugin-interfacemgr-staticdev \
	 ${PN}-lrmtest \
     ${PN}-plugin-compress \
     ${PN}-plugin-compress-dbg \
     ${PN}-plugin-compress-staticdev \
	 "

FILES:${PN} = "${sysconfdir} /var ${libdir}/lib*.so.* ${sbindir} ${datadir}/cluster-glue/*sh ${datadir}/cluster-glue/*pl\
	${libdir}/heartbeat/transient-test.sh \
	${libdir}/heartbeat/logtest \
	${libdir}/heartbeat/ipctransientserver \
	${libdir}/heartbeat/base64_md5_test \
	${libdir}/heartbeat/ipctest \
	${libdir}/heartbeat/ipctransientclient \
	${libdir}/heartbeat/ha_logd \
	${libdir}/heartbeat/lrmd \
	${systemd_unitdir} \
	"

FILES:${PN}-dbg += "${libdir}/heartbeat/.debug/ \
                    ${sbindir}/.debug/ \
                    ${libdir}/.debug/ \
                   "

FILES:${PN}-plugin-compress = "${libdir}/heartbeat/plugins/compress/*.so"
FILES:${PN}-plugin-compress-staticdev = "${libdir}/heartbeat/plugins/compress/*.*a"
FILES:${PN}-plugin-compress-dbg = "${libdir}/heartbeat/plugins/compress/.debug/"

FILES:${PN}-plugin-test = "${libdir}/heartbeat/plugins/test/test.so"
FILES:${PN}-plugin-test-staticdev = "${libdir}/heartbeat/plugins/test/test.*a"
FILES:${PN}-plugin-test-dbg = "${libdir}/heartbeat/plugins/test/.debug/"
FILES:${PN}-plugin-stonith2 = " \
	${libdir}/stonith/plugins/xen0-ha-dom0-stonith-helper \
	${libdir}/stonith/plugins/stonith2/*.so \
	"
FILES:${PN}-plugin-stonith2-ribcl = "${libdir}/stonith/plugins/stonith2/ribcl.py"

FILES:${PN}-plugin-stonith2-dbg = "${libdir}/stonith/plugins/stonith2/.debug/"
FILES:${PN}-plugin-stonith2-staticdev = "${libdir}/stonith/plugins/stonith2/*.*a"

FILES:${PN}-plugin-stonith-external = "${libdir}/stonith/plugins/external/"
FILES:${PN}-plugin-raexec = "${libdir}/heartbeat/plugins/RAExec/*.so"
FILES:${PN}-plugin-raexec-staticdev = "${libdir}/heartbeat/plugins/RAExec/*.*a"
FILES:${PN}-plugin-raexec-dbg = "${libdir}/heartbeat/plugins/RAExec/.debug/"

FILES:${PN}-plugin-interfacemgr = "${libdir}/heartbeat/plugins/InterfaceMgr/generic.so"
FILES:${PN}-plugin-interfacemgr-staticdev = "${libdir}/heartbeat/plugins/InterfaceMgr/generic.*a"
FILES:${PN}-plugin-interfacemgr-dbg = "${libdir}/heartbeat/plugins/InterfaceMgr/.debug/"

FILES:${PN}-lrmtest = "${datadir}/cluster-glue/lrmtest/"

RDEPENDS:${PN} += "perl"
RDEPENDS:${PN}-plugin-stonith2 += "bash"
RDEPENDS:${PN}-plugin-stonith-external += "bash python3-core perl"
RDEPENDS:${PN}-plugin-stonith2-ribcl += "python3-core"
RDEPENDS:${PN}-lrmtest += "${VIRTUAL-RUNTIME_getopt} ${PN}-plugin-raexec"
