#!/usr/bin/env bash
echo "[[ Performing prechecks ]]"
set -e

BASE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$BASE"

if [ "$(whoami)" != "root" ]; then
  echo "This script requires root permissions (sudo)"
  exit 1
fi

# SSH keys
echo "[[ Enabling passwordless remote access ]]"
eval $(ssh-agent) > /dev/null
ssh-add "$HOME/.ssh/id_rsa" > /dev/null

# Fix bash files
find ../.. -name "*.sh" ! -name "deploy_*.sh" -exec dos2unix -q {} \;
find ../.. -name "*.sh" ! -name "deploy_*.sh" -exec chmod +x {} \;

# Export useful constants
export ='/var/lib/abraid'
declare -r ABRAID_SUPPORT_PATH
export WEBAPP_PATH='/var/lib/tomcat7/webapps'
declare -r WEBAPP_PATH

# Apply under-construction page
echo "[[ Applying under-construction page ]]"
rm -f "/etc/nginx/sites-enabled/proxy"
rm -f "/etc/nginx/sites-enabled/maintenance"
ln -s "/etc/nginx/sites-available/maintenance" "/etc/nginx/sites-enabled/maintenance"
service nginx restart > /dev/null

# Stop servlet containers
echo "[[ Stopping services ]]"
service tomcat7 stop > /dev/null
service gunicorn stop > /dev/null
echo -e "#\x21/bin/sh\n\n:" > "/etc/cron.hourly/abraid"

# Checking for dir
if [[ ! -d "$ABRAID_SUPPORT_PATH" ]]; then
  echo "[[ Creating support directory ]]"
  mkdir -p "$ABRAID_SUPPORT_PATH"
  permissionFix "tomcat7:tomcat7" "$ABRAID_SUPPORT_PATH"
fi

# Getting config
echo "[[ Updating ABRAID configuration ]]"
#. up_config.sh "shared" "application"

echo "[[ Loading config ]]"
# Source site specific variables
source "$ABRAID_SUPPORT_PATH/conf/shared/db.conf.sh"
# Source useful functions
source "functions.sh"

# Upgrading database
echo "[[ Upgrading database ]]"
#. up_db.sh

# Upgrading machinelearning
echo "[[ Upgrading machinelearning ]]"
#. up_ml.sh

# Upgrading geoserver
echo "[[ Upgrading geoserver ]]"
#. up_gs.sh

# Upgrading modeloutput
echo "[[ Upgrading modeloutput ]]"
#installWar "MO" "../../ABRAID-MP_ModelOutputHandler.war" "modeloutput"

# Upgrading publicsite
echo "[[ Upgrading publicsite ]]"
#installWar "PS" "../../ABRAID-MP_PublicSite.war" "ROOT"

# Upgrading datamanager
echo "[[ Upgrading datamanager ]]"
#. up_dm.sh

# Waiting
echo "[[ Main updates complete ]]"
read -p "You should now update all modelling servers before continuing. Press [enter] to continue ..."

# Bring services back up
echo "[[ Restarting services ]]"
service gunicorn start > /dev/null
service tomcat7 start > /dev/null
echo -e "#\x21/bin/sh\n\n/var/lib/abraid/datamanager/datamanager.sh" > "/etc/cron.hourly/abraid"

# Remove under-construction page
echo "[[ Removing under-construction page ]]"
rm -f "/etc/nginx/sites-enabled/proxy"
rm -f "/etc/nginx/sites-enabled/maintenance"
ln -s "/etc/nginx/sites-available/proxy" "/etc/nginx/sites-enabled/proxy"
service nginx restart > /dev/null

echo "[[ Done ]]"
echo "You may now delete the artifacts directory"