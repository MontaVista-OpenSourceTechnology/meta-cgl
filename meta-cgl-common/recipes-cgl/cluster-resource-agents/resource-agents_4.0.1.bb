SUMMARY = "OCF resource agents for use by compatible cluster managers"
DESCRIPTION = "A set of scripts to interface with several services \
to operate in a High Availability environment for both Pacemaker and \
rgmanager service managers."
HOMEPAGE = "http://sources.redhat.com/cluster/wiki/"

LICENSE = "GPLv2+ & LGPLv2+ & GPLv3"
LICENSE_${PN} = "GPLv2+ & LGPLv2+"
LICENSE_${PN}-dev = "GPLv2+ & LGPLv2+"
LICENSE_${PN}-staticdev = "GPLv2+ & LGPLv2+"
LICENSE_${PN}-dbg = "GPLv2+ & LGPLv2+"
LICENSE_${PN}-doc = "GPLv2+ & LGPLv2+"
LICENSE_${PN}-extra = "GPLv3"
LICENSE_${PN}-extra-dbg = "GPLv3"
LICENSE_ldirectord = "GPLv2+"

SRC_URI = "https://codeload.github.com/ClusterLabs/resource-agents/tar.gz/v${PV};downloadfilename=${BPN}-${PV}.tar.gz \
           file://01-disable-doc-build.patch \
           file://02-set-OCF_ROOT_DIR-to-libdir-ocf.patch \
           file://03-fix-header-defs-lookup.patch \
           file://fix-install-sh-not-found.patch \
           file://fs.sh-fix-builds-when-srcdir-and-builddir-are-sepera.patch \
          "

SRC_URI[md5sum] = "8530431861e659d4ce2f04afcc4efc03"
SRC_URI[sha256sum] = "863f83c724bad3a8bcff12b9c8712406c43e010041868826cad7b78fd8cfb9fb"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.GPLv3;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "cluster-glue"

# There are many tools and scripts that need bash and perl.
# lvm.sh requires: lvm2
# ip.sh requires: ethtool iproute2 iputils-arping
# fs.sh requires: e2fsprogs-e2fsck util-linux quota
# netfs.sh requires: procps util-linux nfs-utils
RDEPENDS_${PN} += "bash perl lvm2 \
    ethtool iproute2 iputils-arping \
    e2fsprogs-e2fsck util-linux quota \
    procps nfs-utils \
"

inherit autotools systemd pkgconfig

EXTRA_OECONF += "--disable-fatal-warnings \
                 --with-rsctmpdir=/var/run/heartbeat/rsctmp"

do_install_append() {
    rm -rf "${D}${localstatedir}/run"
    rmdir --ignore-fail-on-non-empty "${D}${localstatedir}"
}

# tickle_tcp is published under GPLv3, we just split it into ${PN}-extra,
# and it's required by portblock, so move portblock into ${PN}-extra together.
PACKAGES_prepend  = "${PN}-extra ${PN}-extra-dbg ldirectord "
NOAUTOPACKAGEDEBUG = "1"
FILES_${PN}-extra = "${libexecdir}/heartbeat/tickle_tcp \
                     ${libdir}/ocf/resource.d/heartbeat/portblock \
                     ${datadir}/resource-agents/ocft/configs/portblock \
                    "
FILES_${PN}-extra-dbg = "${libexecdir}/heartbeat/.debug/tickle_tcp"

FILES_ldirectord = " \
        ${sbindir}/ldirectord \
        ${sysconfdir}/ha.d/resource.d/ldirectord \
        ${sysconfdir}/init.d/ldirectord \
        ${sysconfdir}/logrotate.d/ldirectord \
        ${libdir}/ocf/resource.d/heartbeat/ldirectord \
        "
FILES_ldirectord-doc = "${mandir}/man8/ldirectord.8*"

RDEPENDS_ldirectord += " \
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
SYSTEMD_SERVICE_ldirectord += "ldirectord.service"

FILES_${PN} += "${datadir}/cluster/* \
                ${libdir}/ocf/resource.d/heartbeat/ \
                ${libdir}/ocf/lib/heartbeat/* \
                ${libdir}/ocf/resource.d/redhat \
                "

FILES_${PN}-dbg += "${libdir}/ocf/resource.d/heartbeat/.debug \
                    ${sbindir}/.debug \
                    ${libexecdir}/heartbeat/.debug "
