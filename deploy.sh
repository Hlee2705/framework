#!/bin/bash

set -e

# ==========================================
# Configuration
# ==========================================

FRAMEWORK_DIR="$HOME/Documents/Web_Dyn/framework/framework"
TEST_DIR="$HOME/Documents/Web_Dyn/framework/testFramework"

CATALINA_HOME="$HOME/TOMCAT"

WAR_NAME="testFramework.war"

echo "=========================================="
echo " Déploiement du mini framework"
echo "=========================================="

# ==========================================
# 1. Compilation du framework
# ==========================================

echo
echo "[1/5] Compilation du framework..."

cd "$FRAMEWORK_DIR"

mvn clean install

# ==========================================
# 2. Compilation du projet de test
# ==========================================

echo
echo "[2/5] Compilation du projet de test..."

cd "$TEST_DIR"

mvn clean package

# ==========================================
# 3. Arrêt de Tomcat
# ==========================================

echo
echo "[3/5] Arrêt de Tomcat..."

"$CATALINA_HOME/bin/shutdown.sh" || true

sleep 3

# ==========================================
# 4. Déploiement
# ==========================================

echo
echo "[4/5] Déploiement..."

rm -rf "$CATALINA_HOME/webapps/testFramework"
rm -f "$CATALINA_HOME/webapps/$WAR_NAME"

cp "target/$WAR_NAME" "$CATALINA_HOME/webapps/"

# ==========================================
# 5. Démarrage de Tomcat
# ==========================================

echo
echo "[5/5] Démarrage de Tomcat..."

"$CATALINA_HOME/bin/startup.sh"

echo
echo "=========================================="
echo "Déploiement terminé !"
echo "=========================================="
echo
echo "Application :"
echo "http://localhost:8080/testFramework/"