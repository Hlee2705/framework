#!/bin/bash

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
# Compilation du framework
# ==========================================

echo
echo "[1/5] Compilation du framework..."

cd "$FRAMEWORK_DIR" || exit

mvn clean install

if [ $? -ne 0 ]; then
    echo "Erreur lors de la compilation du framework."
    exit 1
fi

# ==========================================
# Compilation du projet de test
# ==========================================

echo
echo "[2/5] Compilation du projet de test..."

cd "$TEST_DIR" || exit

mvn clean package

if [ $? -ne 0 ]; then
    echo "Erreur lors de la compilation du projet de test."
    exit 1
fi

# ==========================================
# Déploiement
# ==========================================

echo
echo "[3/5] Déploiement..."

rm -rf "$CATALINA_HOME/webapps/testFramework"
rm -f "$CATALINA_HOME/webapps/$WAR_NAME"

cp "target/$WAR_NAME" "$CATALINA_HOME/webapps/"

# ==========================================
# Redémarrage de Tomcat
# ==========================================

echo
echo "[4/5] Redémarrage de Tomcat..."

"$CATALINA_HOME/bin/shutdown.sh"

sleep 2

"$CATALINA_HOME/bin/startup.sh"

# ==========================================
# Fin
# ==========================================

echo
echo "[5/5] Déploiement terminé."

echo
echo "Application :"
echo "http://localhost:8080/testFramework/"