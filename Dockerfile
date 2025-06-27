# Multi-stage Docker build for MAEDN Scala game
# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-jammy AS builder

# Install required tools
RUN apt-get update && apt-get install -y \
    curl wget bash \
    && rm -rf /var/lib/apt/lists/*

# Install sbt
RUN wget -O- "https://github.com/sbt/sbt/releases/download/v1.9.8/sbt-1.9.8.tgz" | tar xzf - -C /opt/ && \
    ln -s /opt/sbt/bin/sbt /usr/local/bin/sbt

# Set working directory
WORKDIR /app

# Copy build configuration files first (for better caching)
COPY build.sbt ./
COPY project/ ./project/

# Download dependencies (this layer will be cached if build files don't change)
RUN sbt update

# Copy source code
COPY src/ ./src/

# Build the application with assembly
RUN sbt clean assembly

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre-jammy

# Install required packages for GUI support and X11 forwarding
RUN apt-get update && apt-get install -y \
    libx11-6 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxrandr2 \
    libxcursor1 \
    libxcomposite1 \
    libxdamage1 \
    libxfixes3 \
    libxinerama1 \
    libxss1 \
    libgtk-3-0 \
    libgl1-mesa-glx \
    libgl1-mesa-dri \
    fonts-dejavu-core \
    fontconfig \
    xauth \
    x11-apps \
    bash \
    && rm -rf /var/lib/apt/lists/*

# Create app user for security
RUN useradd -m -s /bin/bash appuser

# Set working directory
WORKDIR /app

# Copy the assembled JAR from builder stage
COPY --from=builder /app/target/scala-3.5.1/maedn-game.jar ./maedn-game.jar

# Copy resources if they exist
COPY --from=builder /app/src/main/resources/ ./resources/

# Create saves directory for game data persistence
RUN mkdir -p /app/saves && chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port for potential web interface (future enhancement)
EXPOSE 8080

# Set JVM options for better container performance
ENV JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC -XX:+UseContainerSupport"

# Default to TUI-only mode in containers (can be overridden)
ENV MAEDN_TUI_ONLY=true

# Default command runs the TUI version
# For GUI mode with X11 forwarding, override the environment variable
CMD ["java", "-jar", "maedn-game.jar"]

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD java -version || exit 1

# Labels for better image management
LABEL maintainer="MAEDN Game Development Team"
LABEL description="Mensch ärgere dich nicht - Scala Edition"
LABEL version="0.1.0-SNAPSHOT"
LABEL source="https://github.com/annaznvc/MAEDN"
