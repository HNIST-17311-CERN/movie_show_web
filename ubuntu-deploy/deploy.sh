#!/usr/bin/env bash
set -euo pipefail

SERVICE=security
APP_DIR=/opt/security
JAR_NAME=springboot-demo-1.0.0.jar
INCOMING_DIR="$HOME/incoming"
INCOMING_JAR="$INCOMING_DIR/$JAR_NAME"
BACKUP_DIR="$APP_DIR/backup"

mkdir -p "$BACKUP_DIR"

if [ ! -f "$INCOMING_JAR" ]; then
  echo "ERROR: no incoming jar at $INCOMING_JAR" >&2
  exit 1
fi

if [ -f "$APP_DIR/$JAR_NAME" ]; then
  cp "$APP_DIR/$JAR_NAME" "$BACKUP_DIR/$JAR_NAME.$(date +%Y%m%d%H%M%S)"
fi

mv "$INCOMING_JAR" "$APP_DIR/$JAR_NAME"

# 前端静态资源同步到 nginx 目录
STATIC_SRC="$INCOMING_DIR"
STATIC_DST="$APP_DIR/src/main/resources"
mkdir -p "$STATIC_DST"
for d in filmlane-master adminkit-web-ui-kit-dashboard-template live2d-example-master; do
  if [ -d "$STATIC_SRC/$d" ]; then
    rm -rf "$STATIC_DST/$d"
    cp -r "$STATIC_SRC/$d" "$STATIC_DST/"
  fi
done

rm -rf "$INCOMING_DIR"

sudo systemctl restart "$SERVICE"
echo "Deploy OK: $(date)"
