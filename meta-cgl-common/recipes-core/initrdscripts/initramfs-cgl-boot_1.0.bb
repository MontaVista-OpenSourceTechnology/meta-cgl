SUMMARY = "Support for having multipath iSCSI devices as root file system"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
SRC_URI = "file://init-boot.sh \
"

do_install() {
        install -m 0755 ${UNPACKDIR}/init-boot.sh ${D}/init
}

inherit allarch

RDEPENDS:${PN} += "multipath-tools kpartx iscsi-initiator-utils"

FILES:${PN} += " /init "
