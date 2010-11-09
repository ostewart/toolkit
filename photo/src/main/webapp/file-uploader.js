function createFileUploader(basePath) {
    var files = [];
    var maxTries = 3;

    function collect(array, fn) {
        var results = [];
        for (i=0; i<array.length; i++) {
            if (fn(array[i])) {
                results.push(array[i]);
            }
        }
        return results;
    }

    return {
        loadPreviews: false,
        previewLoadedHook: function(file, fileData) {
        },
        addFile: function(index, file, frameNumber) {
            files.push({
                index: index,
                file: file,
                name: fileNameFrom(file),
                tries: 0,
                isComplete: false,
                frameNumber: frameNumber,
                isPreviewLoaded: false
            });
        },
        addFiles: function(files, firstFrameNumber) {
            for (i = 0; i < files.length; i++) {
                this.addFile(i, files[i], firstFrameNumber++);
            }
        },
        completedFiles: function() {
            return collect(files, function(file) {
                return file.isComplete;
            });
        },
        pendingFiles: function() {
            return collect(files, function(file) {
                return !file.isComplete;
            });
//            var pendingFiles = [];
//            for (i=0; i<files.length;i++) {
//                if (!files[i].isComplete) {
//                    pendingFiles.push(files[i]);
//                }
//            }
//            return pendingFiles;
        },
        start: function() {
            this.startNextPreviewLoad();
        },
        startNextPreviewLoad: function() {
            for (i = 0; i < files.length; i++) {
                if (!files[i].isPreviewLoaded) {
                    if (!this.loadPreviews) {
                        this.previewLoadFinishedHandler(files[i])(null);
                        return;
                    } else {
                        var previewReader = new FileReader();
                        var previewLoadFinishedHandler = this.previewLoadFinishedHandler(files[i]);
                        previewReader.addEventListener("loadend", previewLoadFinishedHandler, false);
                        previewReader.readAsDataURL(files[i].file);
                        return;
                    }
                }
            }
            this.startNextUpload(); //after all the previews are loaded
        },
        previewLoadFinishedHandler: function(file) {
            var that = this;
            return function(event) {
                file.isPreviewLoaded = true;
                var fileData = that.loadPreviews ? event.target.result : null;
                that.previewLoadedHook(file, fileData);
                that.startNextPreviewLoad();
            }
        },
        startNextUpload: function() {
            for (i = 0; i < files.length; i++) {
                if (!files[i].isComplete && files[i].tries < maxTries) {
                    this.beginUpload(i, files[i]);
                    return;
                }
            }
        },
        readFileWithHandler: function(file, handler) {
            var uploadReader = new FileReader();
            uploadReader.addEventListener("loadend", handler, false);
            uploadReader.readAsBinaryString(file);
        },
        beginUpload: function(index, file) {
            this.readFileWithHandler(file.file, this.uploadPhotoHandler(file));
        },
        beforeUploadHook: function (file) {
        },
        uploadProgressHandler: function() {
        },
        uploadErrorHandler: function() {
        },
        newXhr: function() {
            return new XMLHttpRequest();
        },
        uploadPhotoHandler: function(file) {
            var that = this;
            return function(event) {
                that.beforeUploadHook(file);
                file.tries++;

                var xhr = that.newXhr();
                xhr.upload.addEventListener("progress", that.uploadProgressHandler, false);
                xhr.upload.addEventListener("load", that.uploadCompleteHandler(xhr, file), false);
                xhr.upload.addEventListener("error", that.uploadErrorHandler, false);

                xhr.open("POST", basePath + "?pos=" + file.frameNumber + "&fileName=" + fileNameFrom(file.file));
                xhr.sendAsBinary(event.target.result);
            };
        },
        uploadRetryHook: function (file) {
        },
        fileCompleteHook: function(file) {
        },
        uploadFailedHook: function(file, xhr) {
        },
        uploadCompleteHandler: function(xhr, file) {
            var that = this;
            return function(event) {
                var imageIndex = file.index;

                if (xhr.status != 200) {
                    if (file.tries < maxTries) {
                        var cooloffMillis = 200;
                        that.uploadRetryHook(file);
                        setTimeout(function() {
                            that.uploadPhotoHandler(file)(event, imageIndex, file.tries, fileNameFrom(file.file));
                        }, cooloffMillis);
                    } else {
                        that.uploadFailedHook(file, xhr);
                    }
                } else {
                    file.isComplete = true;
                    that.fileCompleteHook(file);
                    that.startNextUpload();
                }
            }
        }
    };
}

function fileNameFrom(file) {
    var fileName;
    if (file.fileName === undefined) {
        fileName = file.name;
    } else {
        fileName = file.fileName;
    }
    return fileName;
}
