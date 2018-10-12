DESCRIPTION = "Cluster Glue is a set of libraries, tools and utilities suitable for \
the Heartbeat/Pacemaker cluster stack. In essence, Glue is everything that \
is not the cluster messaging layer (Heartbeat), nor the cluster resource manager \
(Pacemaker), nor a Resource Agent."
HOMEPAGE = "http://clusterlabs.org/"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://COPYING.LIB;md5=243b725d71bb5df4a1e5920b344b86ad \
"

DEPENDS = "libxml2 libtool glib-2.0 bzip2 util-linux net-snmp openhpi"

SRC_URI = " \
    http://hg.linux-ha.org/glue/archive/glue-${PV}.tar.bz2 \
    file://0001-don-t-compile-doc-and-Error-Fix.patch \
    file://0001-ribcl.py.in-Warning-Fix.patch \
    file://volatiles \
    file://tmpfiles \
"
SRC_URI_append_libc-uclibc = " file://kill-stack-protector.patch"

SRC_URI[md5sum] = "ec620466d6f23affa3b074b72bca7870"
SRC_URI[sha256sum] = "feba102fa1e24b6be2005089ebe362b82d6567af60005cf371679b1b44ec503f"

inherit autotools useradd pkgconfig systemd

SYSTEMD_SERVICE_${PN} = "logd.service"
SYSTEMD_AUTO_ENABLE = "disable"

HA_USER = "hacluster"
HA_GROUP = "haclient"

S = "${WORKDIR}/Reusable-Cluster-Components-glue--glue-${PV}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_system_unitdir},--without-systemdsystemunitdir,systemd"

EXTRA_OECONF = "--with-daemon-user=${HA_USER} \
                --with-daemon-group=${HA_GROUP} \
                --disable-fatal-warnings \
                --with-ocf-root=${libdir}/ocf \
               "

CACHED_CONFIGUREVARS="ac_cv_path_XML2CONFIG=0"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--home-dir=${localstatedir}/lib/heartbeat/cores/${HA_USER} \
                       -g ${HA_GROUP} -r -s ${sbindir}/nologin -c 'cluster user' ${HA_USER} \
                      "
GROUPADD_PARAM_${PN} = "-r ${HA_GROUP}"

do_configure_prepend() {
    ln -sf ${PKG_CONFIG_SYSROOT_DIR}/usr/include/libxml2/libxml ${PKG_CONFIG_SYSROOT_DIR}/usr/include/libxml
}

do_install_append() {
	install -d ${D}${sysconfdir}/default/volatiles
	install -m 0644 ${WORKDIR}/volatiles ${D}${sysconfdir}/default/volatiles/04_cluster-glue
	install -d ${D}${sysconfdir}/tmpfiles.d
	install -m 0644 ${WORKDIR}/tmpfiles ${D}${sysconfdir}/tmpfiles.d/${PN}.conf
}

pkg_postinst_${PN} () {
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

FILES_${PN} = "${sysconfdir} /var ${libdir}/lib*.so.* ${sbindir} ${datadir}/cluster-glue/*sh ${datadir}/cluster-glue/*pl\
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

FILES_${PN}-dbg += "${libdir}/heartbeat/.debug/ \
                    ${sbindir}/.debug/ \
                    ${libdir}/.debug/ \
                   "

FILES_${PN}-plugin-compress = "${libdir}/heartbeat/plugins/compress/*.so"
FILES_${PN}-plugin-compress-staticdev = "${libdir}/heartbeat/plugins/compress/*.*a"
FILES_${PN}-plugin-compress-dbg = "${libdir}/heartbeat/plugins/compress/.debug/"

FILES_${PN}-plugin-test = "${libdir}/heartbeat/plugins/test/test.so"
FILES_${PN}-plugin-test-staticdev = "${libdir}/heartbeat/plugins/test/test.*a"
FILES_${PN}-plugin-test-dbg = "${libdir}/heartbeat/plugins/test/.debug/"
FILES_${PN}-plugin-stonith2 = " \
	${libdir}/stonith/plugins/xen0-ha-dom0-stonith-helper \
	${libdir}/stonith/plugins/stonith2/*.so \
	"
FILES_${PN}-plugin-stonith2-ribcl = "${libdir}/stonith/plugins/stonith2/ribcl.py"

FILES_${PN}-plugin-stonith2-dbg = "${libdir}/stonith/plugins/stonith2/.debug/"
FILES_${PN}-plugin-stonith2-staticdev = "${libdir}/stonith/plugins/stonith2/*.*a"

FILES_${PN}-plugin-stonith-external = "${libdir}/stonith/plugins/external/"
FILES_${PN}-plugin-raexec = "${libdir}/heartbeat/plugins/RAExec/*.so"
FILES_${PN}-plugin-raexec-staticdev = "${libdir}/heartbeat/plugins/RAExec/*.*a"
FILES_${PN}-plugin-raexec-dbg = "${libdir}/heartbeat/plugins/RAExec/.debug/"

FILES_${PN}-plugin-interfacemgr = "${libdir}/heartbeat/plugins/InterfaceMgr/generic.so"
FILES_${PN}-plugin-interfacemgr-staticdev = "${libdir}/heartbeat/plugins/InterfaceMgr/generic.*a"
FILES_${PN}-plugin-interfacemgr-dbg = "${libdir}/heartbeat/plugins/InterfaceMgr/.debug/"

FILES_${PN}-lrmtest = "${datadir}/cluster-glue/lrmtest/"

RDEPENDS_${PN} += "perl"
RDEPENDS_${PN}-plugin-stonith2 += "bash"
RDEPENDS_${PN}-plugin-stonith-external += "bash python perl"
RDEPENDS_${PN}-plugin-stonith2-ribcl += "python"
RDEPENDS_${PN}-lrmtest += "${VIRTUAL-RUNTIME_getopt} ${PN}-plugin-raexec"
