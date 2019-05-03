Summanry = "Red Hat Cluster"
DESCRIPTION = "Red Hat Cluster"
SECTION = "libs"
HOMEPAGE = "https://fedorahosted.org/cluster/wiki/HomePage"

SRC_URI = "https://releases.pagure.org/linux-cluster/cluster/${BP}.tar.xz \
           file://0001-Fix-the-kernel-source-patch.patch"


SRC_URI[md5sum] = "300d83dbbc525c3da21c2e961271c84b"
SRC_URI[sha256sum] = "4d340338c2376d369cb223469fa1a3356cce9ab5b2a0a0a33256ade2dbbe02d1"

LICENSE = "GPL-2.0 & LGPL-2.0"
LIC_FILES_CHKSUM = "file://doc/README.licence;md5=ee8ae43af5ea09f12ca7f7a649764cb0"

PR = "r1"

DEPENDS = "corosync dbus openldap libxml2 ncurses perl zlib"

FILES_${PN} += "/lib/udev/rules.d/51-dlm.rules \
    ${localstatedir}/run "
FILES_${PN}-doc += "/usr/share/man3/* /usr/share/man8/*"

do_configure () {
    CFLAGS="${TARGET_CFLAGS}"       \
    CCFLAGS="${TARGET_CFLAGS}"      \
    CXXFLAGS="${TARGET_CFLAGS}"     \
    ./configure \
        --without_rgmanager \
        --disable_kernel_check \
        --without_cman \
        --without_fence \
        --without_bindings \
        --without_group \
        --without_config \
        --without_common \
        --without_dlm \
        2>&1
}

do_compile () {
    pwd
    CFLAGS="${TARGET_CFLAGS}" \
    make libdir=${STAGING_LIBDIR} incdir=${STAGING_INCDIR}          \
    CC=${TARGET_SYS}-gcc
}
do_install () {
    rm -rf ${D}
    make install DESTDIR=${D} libdir=${D}/usr/lib sbindir=${D}/usr/sbin \
                 mandir=${D}/usr/share docdir=${D}/usr/share/doc

    ## tree fix up
    # /etc/sysconfig/cman
    mkdir -p ${D}/etc/sysconfig

    # logrotate name
    mv ${D}/etc/logrotate.d/cluster ${D}/etc/logrotate.d/cman
    # fix library permissions or strip helpers won't work.
    find ${D} -name "lib*.so.*" -exec chmod 0755 {} \;
    # fix lcrso permissions or strip helpers won't work.
    find ${D} -name "*.lcrso" -exec chmod 0755 {} \;
    # remove docs
    rm -rf ${D}${datadir}/doc/cluster
    rm -rf ${D}${localstatedir}/run
    rm -rf ${D}${localstatedir}/lib
    rm -rf ${D}${localstatedir}/log
    rmdir --ignore-fail-on-non-empty ${D}${localstatedir}
}
