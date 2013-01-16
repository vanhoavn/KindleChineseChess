#!/bin/sh

# Kindle 3.1 Jailbreak
# Created by Yifan Lu
# http://yifan.lu/

HACKNAME="jailbreak"
HOME_DIR="/mnt/us"
KEY_DIR=/etc/uks
INIT_DIR="/etc/init.d"
SLEVEL="64"
KLEVEL="09"

_FUNCTIONS=/etc/rc.d/functions
[ -f ${_FUNCTIONS} ] && . ${_FUNCTIONS}

MSG_SLLVL_D="debug"
MSG_SLLVL_I="info"
MSG_SLLVL_W="warn"
MSG_SLLVL_E="err"
MSG_SLLVL_C="crit"
MSG_SLNUM_D=0
MSG_SLNUM_I=1
MSG_SLNUM_W=2
MSG_SLNUM_E=3
MSG_SLNUM_C=4
MSG_CUR_LVL=/var/local/system/syslog_level

logmsg()
{
    local _NVPAIRS
    local _FREETEXT
    local _MSG_SLLVL
    local _MSG_SLNUM

    _MSG_LEVEL=$1
    _MSG_COMP=$2

    { [ $# -ge 4 ] && _NVPAIRS=$3 && shift ; }

    _FREETEXT=$3

    eval _MSG_SLLVL=\${MSG_SLLVL_$_MSG_LEVEL}
    eval _MSG_SLNUM=\${MSG_SLNUM_$_MSG_LEVEL}

    local _CURLVL

    { [ -f $MSG_CUR_LVL ] && _CURLVL=`cat $MSG_CUR_LVL` ; } || _CURLVL=1

    if [ $_MSG_SLNUM -ge $_CURLVL ]; then
        /usr/bin/logger -p local4.$_MSG_SLLVL -t "ota_install" "$_MSG_LEVEL def:$_MSG_COMP:$_NVPAIRS:$_FREETEXT"
    fi

    if [ "$_MSG_LEVEL" != "D" ]; then
      echo "ota_install: $_MSG_LEVEL def:$_MSG_COMP:$_NVPAIRS:$_FREETEXT"
#      [ -d /mnt/us/$HACKNAME ] && echo "ota_install: $_MSG_LEVEL def:$_MSG_COMP:$_NVPAIRS:$_FREETEXT" >> $LOG_FILE
    fi
}

uninstall_previous()
{
	if [ -f "/etc/init.d/switch-updates-provider" ]; then
		logmsg "I" "update" "switch-updates-provider hack found, uninstalling"

		rm -f /etc/init.d/switch-updates-provider

		# Remove rc symlinks
		for RC in 0 1 2 3 4 5 6
		do
		    rm -f /etc/rc$RC.d/S25switch-updates-provider
		done

		# Restore original files
		mv -f "$KEY_DIR/pubprodkey01.pem.original" "$KEY_DIR/pubprodkey01.pem"
		mv -f "$KEY_DIR/pubprodkey02.pem.original" "$KEY_DIR/pubprodkey02.pem"

		# Remove the keys we copied
		rm -f "$KEY_DIR/pubprodkey01.pem.hack"
		rm -f "$KEY_DIR/pubprodkey02.pem.hack"
		rm -f "$KEY_DIR/pubprodkey01.pem.amazon"
		rm -f "$KEY_DIR/pubprodkey02.pem.amazon"

		[ -d "$HOME_DIR/updates-provider" ] && rm -rf "$HOME_DIR/updates-provider"
	fi
	if [ -f "$INIT_DIR/$HACKNAME" ]; then
		logmsg "I" "update" "old jailbreak version found, uninstalling"
		# Remove old version of hack
		[ -d "$HOME_DIR/jailbreak" ] && rm -rf "$HOME_DIR/jailbreak"
		[ -f "$HOME_DIR/ENABLE_HACK_UPDATES" ] && rm -f "$HOME_DIR/ENABLE_HACK_UPDATES"
		rm -f "$KEY_DIR/pubprodkey01.hack.pem"
		rm -f "$KEY_DIR/pubprodkey02.hack.pem"
		rm -f "$KEY_DIR/pubprodkey01.amazon.pem"
		rm -f "$KEY_DIR/pubprodkey02.amazon.pem"
		rm -f "$INIT_DIR/$HACKNAME"
		for RC in 0 1 2 3 4 5 6
		do
		    rm -f "/etc/rc$RC.d/S25$HACKNAME"
		done
		
		mv "$KEY_DIR/pubprodkey01.pem.original" "$KEY_DIR/pubprodkey01.pem"
		mv "$KEY_DIR/pubprodkey02.pem.original" "$KEY_DIR/pubprodkey02.pem"
	fi
}

# Initialized
update_progressbar 10

# Check to make sure we aren't conflicting with NiLuJe's jailbreak
if [ -f $INIT_DIR/linkjail ]; then
	logmsg "E" "update" "Another jailbreak is already installed. Update not required."
	return 1
fi
update_progressbar 20

# Remove update provider hack if installed
logmsg "I" "update" "removing old versions, if any"
uninstall_previous
update_progressbar 40

# Extract payload
tar xzf payload.sig -C . 2>&1 >/dev/null
update_progressbar 50

logmsg "I" "update" "create jailbreak init script"
cp -f jailbreak $INIT_DIR/$HACKNAME
chmod a+x $INIT_DIR/$HACKNAME
ln -fs $INIT_DIR/${HACKNAME} /etc/rc5.d/S${SLEVEL}${HACKNAME}
ln -fs $INIT_DIR/${HACKNAME} /etc/rc3.d/K${KLEVEL}${HACKNAME}
update_progressbar 70

logmsg "I" "update" "copy the keys"
cp -f pubprodkey01.hack.pem "$KEY_DIR/"
update_progressbar 90

logmsg "I" "update" "done"
update_progressbar 100

return 0
