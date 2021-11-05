SUMMARY = "Pacemaker command line interface for management and configuration"
DESCRIPTION = "crm shell, a Pacemaker command line interface for management and configuration"

HOMEPAGE = "https://crmsh.github.io"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "asciidoc-native \
           docbook-xsl-stylesheets-native \
           libxslt-native \
           "
RDEPENDS:${PN} = "pacemaker python3-lxml python3-parallax gawk bash python3-doctest"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/ClusterLabs/${BPN}.git;branch=master;protocol=https \
           file://tweaks_for_build.patch \
           file://0001-orderedset.py-fix-deprecation-on-collections.Mutable.patch \
          "

SRCREV = "00ec69054edecd068deda54c6184c0385d90ebd2"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))$"

inherit autotools-brokensep setuptools3

export HOST_SYS
export BUILD_SYS

# Allow to process DocBook documentations without requiring
# network accesses for the dtd and stylesheets
export SGML_CATALOG_FILES = "${STAGING_DATADIR_NATIVE}/xml/docbook/xsl-stylesheets/catalog.xml"

FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR}/${BPN}"
