# Dockerfile for creating a statically-linked Rust application using Dockerfile's
# multi-stage build feature. This also leverages the Dockerfile build cache to avoid
# re-downloading dependencies if they have not changed.
#
FROM messense/rust-musl-cross:x86_64-musl AS build
#FROM messense/rust-musl-cross:arm-musleabi AS build
WORKDIR /usr/src

ENV RUSTUP_IO_THREADS disabled

# Download the target for static linking.

# RUN rustup install stable 
RUN rustup target add x86_64-unknown-linux-musl --toolchain stable 
RUN rustup show
#RUN rustup update stable && rustup target add arm-unknown-linux-musleabi --toolchain stable && rustup show
RUN rustup default stable-x86_64-unknown-linux-gnu && rustup show
# RUN rustup default nightly arm-unknown-linux-musleabi && rustup show
# RUN rustup show

# Create a dummy project and build the app's dependencies.
# If the Cargo.toml or Cargo.lock files have not changed,
# we can use the Dockerfile build cache and skip these (typically slow) steps.
RUN USER=root cargo new operator
RUN mkdir /compiled
WORKDIR /usr/src/operator
COPY Cargo.toml Cargo.lock ./
RUN cargo build --release

# Copy the source and build the application.
COPY src ./src
RUN cargo install --target x86_64-unknown-linux-musl --path ./ --root /compiled
#RUN cargo install --target arm-unknown-linux-musleabi --path ./ --root /compiled

# Copy the statically-linked binary into a scratch container.
FROM scratch
WORKDIR /
COPY --from=build /compiled /
USER 1000

EXPOSE 8080/tcp
CMD ["bin/operator"]
