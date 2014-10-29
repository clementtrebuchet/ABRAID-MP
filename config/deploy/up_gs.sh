#!/usr/bin/env bash
set -e
GS_TEMP_DIR="$(mktemp -d)"
GS_UPDATE_CMD="fileAsk"

setupTempConfigFiles() {
  echo "${deploy_props[geoserver.root.password.hash]}" > "$GS_TEMP_DIR/passwd"
  cp "$WEBAPP_PATH/geoserver/data/security/usergroup/default/users.xml" "$GS_TEMP_DIR/users.xml"
  sed -i "s|password=\".*\"|password=\"${deploy_props[geoserver.admin.password.hash]}\"|g" "$GS_TEMP_DIR/users.xml"
}

setupTempWorkspaceFiles() {
  cp -r "../geoserver/abraid" "$GS_TEMP_DIR/workspace"
  sed -i "s/USER\_REPLACE/${db_props[jdbc.username]}/g" "$GS_TEMP_DIR/workspace/abraid-db/datastore.xml"
  sed -i "s/PW\_REPLACE/${db_props[jdbc.password]}/g" "$GS_TEMP_DIR/workspace/abraid-db/datastore.xml"
  sed -i "s/DB\_REPLACE/${db_props[jdbc.database.name]}/g" "$GS_TEMP_DIR/workspace/abraid-db/datastore.xml"
  sed -i "s/PORT\_REPLACE/${db_props[jdbc.database.port]}/g" "$GS_TEMP_DIR/workspace/abraid-db/datastore.xml"
  sed -i "s/HOST\_REPLACE/${db_props[jdbc.database.host]}/g" "$GS_TEMP_DIR/workspace/abraid-db/datastore.xml"
}

echo "[[ GS | Loading configuration ]]"
TEMP_FILE=$(mktemp)
declare -A db_props
cat "$ABRAID_SUPPORT_PATH/conf/application/database.properties" | grep -v "^#" | grep -v '^[[:space:]]*$' > "$TEMP_FILE"
while read -r line; do
  [[ $line = *=* ]] || continue
  db_props[${line%%=*}]=${line#*=}
done < "$TEMP_FILE"
declare -A deploy_props
cat "$ABRAID_SUPPORT_PATH/conf/application/deployment.properties" | grep -v "^#" | grep -v '^[[:space:]]*$' > "$TEMP_FILE"
while read -r line; do
  [[ $line = *=* ]] || continue
  deploy_props[${line%%=*}]=${line#*=}
done < "$TEMP_FILE"
rm -f "$TEMP_FILE"

echo "[[ GS | Performing prechecks ]]"
: "${db_props[jdbc.database.name]:?"Variable must be set"}"
: "${db_props[jdbc.database.host]:?"Variable must be set"}"
: "${db_props[jdbc.database.port]:?"Variable must be set"}"
: "${db_props[jdbc.username]:?"Variable must be set"}"
: "${db_props[jdbc.password]:?"Variable must be set"}"
: "${deploy_props[geoserver.root.password.hash]:?"Variable must be set"}"
: "${deploy_props[geoserver.admin.password.hash]:?"Variable must be set"}"

echo "[[ GS | Checking for existing GeoServer installation ]]"
if [[ ! -d "$WEBAPP_PATH/geoserver" ]]; then
  echo "No GeoServer install found"
  echo "[[ GS | Downloading GeoServer 2.5.1 ]]"
  curl -# -L "http://sourceforge.net/projects/geoserver/files/GeoServer/2.5.1/geoserver-2.5.1-war.zip" -o "$GS_TEMP_DIR/geoserver-2.5.1-war.zip"
  unzip -p "$GS_TEMP_DIR/geoserver-2.5.1-war.zip" "geoserver.war" > "$GS_TEMP_DIR/geoserver.war"
  rm -f "$GS_TEMP_DIR/geoserver-2.5.1-war.zip"

  echo "[[ GS | Installing GeoServer 2.5.1 ]]"
  unzip -q "$GS_TEMP_DIR/geoserver.war" -d "$WEBAPP_PATH/geoserver"

  echo "[[ GS | Removing default setup ]]"
  rm -rf "$WEBAPP_PATH/geoserver/data/workspaces/"
  mkdir -p "$WEBAPP_PATH/geoserver/data/workspaces/"
  rm -rf "$WEBAPP_PATH/geoserver/data/styles/"
  mkdir -p "$WEBAPP_PATH/geoserver/data/styles/"
  rm -rf "$WEBAPP_PATH/geoserver/data/palettes/"
  mkdir -p "$WEBAPP_PATH/geoserver/data/palettes/"
  rm -rf "$WEBAPP_PATH/geoserver/data/layergroups/"
  mkdir -p "$WEBAPP_PATH/geoserver/data/layergroups/"
  rm -rf "$WEBAPP_PATH/geoserver/data/data/"
  mkdir -p "$WEBAPP_PATH/geoserver/data/data/"
  rm -rf "$WEBAPP_PATH/geoserver/data/coverages/"
  mkdir -p "$WEBAPP_PATH/geoserver/data/coverages/"

  GS_UPDATE_CMD="fileCopy"
else
  echo "There appears to be appears be an existing GeoServer install"
  echo "[[ GS | Skipping GeoServer installation ]]"
  echo "Don't forget you may need to update your GeoServer version (manually)"
fi

echo "[[ GS | Customizing/checking geoserver config ]]"
setupTempConfigFiles
$GS_UPDATE_CMD "$GS_TEMP_DIR/passwd" "$WEBAPP_PATH/geoserver/data/security/masterpw/default/passwd" "GeoServer root password"
$GS_UPDATE_CMD "$GS_TEMP_DIR/users.xml" "$WEBAPP_PATH/geoserver/data/security/usergroup/default/users.xml" "GeoServer admin password"
$GS_UPDATE_CMD "../geoserver/logging.xml" "$WEBAPP_PATH/geoserver/data/logging.xml" "GeoServer logging config"
$GS_UPDATE_CMD "../geoserver/ABRAID_LOGGING.properties" "$WEBAPP_PATH/geoserver/data/logs/ABRAID_LOGGING.properties" "ABRAID GeoServer logging settings"
$GS_UPDATE_CMD "../geoserver/gwc-gs.xml" "$WEBAPP_PATH/geoserver/data/gwc-gs.xml" "GeoServer geo-web-cache config"

echo "[[ GS | Adding/checking the abraid workspace ]]"
setupTempWorkspaceFiles
export GS_UPDATE_CMD
export GS_TEMP_DIR
export WEBAPP_PATH
( cd "$GS_TEMP_DIR/workspace" && find . -type "f" -exec bash -c '"$GS_UPDATE_CMD" "$GS_TEMP_DIR/workspace/$0" "$WEBAPP_PATH/geoserver/data/workspaces/abraid/$0"' {} \; )

echo "[[ GS | Ensuring correct file permissions ]]"
permissionFix "tomcat7:tomcat7" "$WEBAPP_PATH/geoserver/"

echo "[[ GS | Done ]]"
rm -rf "$GS_TEMP_DIR"
