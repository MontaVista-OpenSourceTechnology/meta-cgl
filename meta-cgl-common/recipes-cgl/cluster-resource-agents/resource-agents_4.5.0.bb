SUMMARY = "OCF resource agents for use by compatible cluster managers"
DESCRIPTION = "A set of scripts to interface with several services \
to operate in a High Availability environment for both Pacemaker and \
rgmanager service managers."
HOMEPAGE = "http://sources.redhat.com/cluster/wiki/"

LICENSE = "GPL-2.0-or-later & LGPL-2.0-or-later & GPL-3.0-only"
LICENSE:${PN} = "GPL-2.0-or-later & LGPL-2.0-or-later"
LICENSE:${PN}-dev = "GPL-2.0-or-later & LGPL-2.0-or-later"
LICENSE:${PN}-staticdev = "GPL-2.0-or-later & LGPL-2.0-or-later"
LICENSE:${PN}-dbg = "GPL-2.0-or-later & LGPL-2.0-or-later"
LICENSE:${PN}-doc = "GPL-2.0-or-later & LGPL-2.0-or-later"
LICENSE:${PN}-extra = "GPL-3.0-only"
LICENSE:${PN}-extra-dbg = "GPL-3.0-only"
LICENSE:ldirectord = "GPL-2.0-or-later"

SRC_URI = "git://github.com/ClusterLabs/resource-agents;branch=main;protocol=https \
           file://01-disable-doc-build.patch \
           file://02-set-OCF_ROOT_DIR-to-libdir-ocf.patch \
           file://03-fix-header-defs-lookup.patch \
           file://fix-install-sh-not-found.patch \
           file://0001-ldirectord.service.in-use-run-instead-of-var-run.patch \
           file://0001-ldirectord.service.in-set-correct-path-of-rm-command.patch \
          "

SRCREV = "fee181320547365d7f8c88cca2b32801412b933d" 

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

S="${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.GPLv3;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "cluster-glue"

# There are many tools and scripts that need bash and perl.
# lvm.sh requires: lvm2
# ip.sh requires: ethtool iproute2 iputils-arping
# fs.sh requires: e2fsprogs-e2fsck util-linux quota
# netfs.sh requires: procps util-linux nfs-utils
RDEPENDS:${PN} += "bash perl lvm2 \
    ethtool iproute2 iputils-arping \
    e2fsprogs-e2fsck util-linux quota \
    procps nfs-utils \
"

inherit autotools systemd pkgconfig

CACHED_CONFIGUREVARS += " \
    ac_cv_path_GREP=grep \
    ac_cv_path_TEST=test \
    ac_cv_path_BASH_SHELL=/bin/bash \
    ac_cv_path_PYTHON="/usr/bin/env python3" \
    ac_cv_path_SSH="/usr/bin/ssh" \
"

EXTRA_OECONF += "--disable-fatal-warnings \
                 --with-rsctmpdir=/var/run/heartbeat/rsctmp"

do_install:append() {
    rm -rf ${D}${localstatedir}/run
    rm -rf ${D}${localstatedir}
}

# tickle_tcp is published under GPLv3, we just split it into ${PN}-extra,
# and it's required by portblock, so move portblock into ${PN}-extra together.
PACKAGES:prepend  = "${PN}-extra ${PN}-extra-dbg ldirectord "
NOAUTOPACKAGEDEBUG = "1"
FILES:${PN}-extra = "${libexecdir}/heartbeat/tickle_tcp \
                     ${libdir}/ocf/resource.d/heartbeat/portblock \
                     ${datadir}/resource-agents/ocft/configs/portblock \
                    "
FILES:${PN}-extra-dbg = "${libexecdir}/heartbeat/.debug/tickle_tcp"

FILES:ldirectord = " \
        ${sbindir}/ldirectord \
        ${sysconfdir}/ha.d/resource.d/ldirectord \
        ${sysconfdir}/init.d/ldirectord \
        ${sysconfdir}/logrotate.d/ldirectord \
        ${libdir}/ocf/resource.d/heartbeat/ldirectord \
        "
FILES:ldirectord-doc = "${mandir}/man8/ldirectord.8*"

RDEPENDS:ldirectord += " \
        ipvsadm \
        libdbi-perl \
        libdigest-hmac-perl \
        libmailtools-perl \
        libnet-dns-perl \
        libsocket6-perl \
        libwww-perl \
        perl \
        perl-module-getopt-long \
        perl-module-net-ftp \
        perl-module-net-smtp \
        perl-module-pod-usage \
        perl-module-posix \
        perl-module-socket \
        perl-module-strict \
        perl-module-sys-hostname \
        perl-module-sys-syslog \
        perl-module-vars \
        "

SYSTEMD_PACKAGES = "ldirectord"
SYSTEMD_SERVICE:ldirectord += "ldirectord.service"

FILES:${PN} += "${datadir}/cluster/* \
                ${libdir}/ocf/resource.d/heartbeat/ \
                ${libdir}/ocf/lib/heartbeat/* \
                ${libdir}/ocf/resource.d/redhat \
                ${nonarch_libdir}/tmpfiles.d \
                ${systemd_unitdir}/system \
                "

FILES:${PN}-dbg += "${libdir}/ocf/resource.d/heartbeat/.debug \
                    ${sbindir}/.debug \
                    ${libexecdir}/heartbeat/.debug "
