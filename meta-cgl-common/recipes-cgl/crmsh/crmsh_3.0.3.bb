SUMMARY = "Pacemaker command line interface for management and configuration"
DESCRIPTION = "crm shell, a Pacemaker command line interface for management and configuration"

HOMEPAGE = "https://crmsh.github.io"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "asciidoc-native \
           docbook-xsl-stylesheets-native \
           libxslt-native \
           python-setuptools-native \
           "
RDEPENDS_${PN} = "pacemaker python-lxml gawk"

SRC_URI = "https://github.com/ClusterLabs/crmsh/archive/${PV}.tar.gz;downloadfilename=${BP}.tar.gz \
           file://tweaks_for_build.patch \
          "

SRC_URI[md5sum] = "563b463155a652820c8d1319418e88b7"
SRC_URI[sha256sum] = "364fadb0d0c9619676e10bb75f4ea833a49019aa77ddb26229bc9d1c435585c0"

inherit autotools-brokensep distutils-base

export HOST_SYS
export BUILD_SYS

# Allow to process DocBook documentations without requiring
# network accesses for the dtd and stylesheets
export SGML_CATALOG_FILES = "${STAGING_DATADIR_NATIVE}/xml/docbook/xsl-stylesheets/catalog.xml"

FILES_${PN} += "${PYTHON_SITEPACKAGES_DIR}/${BPN}"
