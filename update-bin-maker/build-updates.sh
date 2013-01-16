#! /bin/sh

# Kindle 3.1 and below Jailbreak
# Created by Yifan Lu
# http://yifan.lu/

HACKNAME="jailbreak"
PKGNAME="${HACKNAME}"
PKGVER="0.4"

echo "NOTICE: Currently, you need to find the MD5 and block size of install.sh and manually input it into loader.sig as jailbreak.sig. I might write something automatic in the future."
sleep 5

# Stupid OSX tries to copy . files
export COPY_EXTENDED_ATTRIBUTES_DISABLE=true

KINDLE_MODELS="k2 k2i dx dxi dxg k3g k3w k3gb"
for model in ${KINDLE_MODELS} ; do
	# Prepare our files for this specific kindle model...
	ARCH=${PKGNAME}_${PKGVER}_${model}

	# Build payload
	tar czf ./payload.sig -C ./payload jailbreak pubprodkey01.hack.pem
	cp install.sh jailbreak.sig
	chmod +x jailbreak.sig
	# Package installer
	tar czf ./installer.tar.gz jailbreak.sig payload.sig loader.sig update\ loader.sig\ .dat update\ loader.sig\ .dat.sig
	# Create installer
	./kindle_update_tool.py c --${model} ${ARCH}_install installer.tar.gz
	# Build uninstall update
	./kindle_update_tool.py m --sign --${model} ${ARCH}_uninstall uninstall.sh
done

# Pack the updates
zip ../${PKGNAME}_${PKGVER}.zip *.bin README.txt
rm -f *.bin
rm -f installer.tar.gz
rm -f payload.sig
rm -f jailbreak.sig