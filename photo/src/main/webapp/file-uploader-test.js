//noinspection ThisExpressionReferencesGlobalObjectJS
var global = this;


describe("FileUploader", function() {
    var uploader;

    beforeEach(function() {
        uploader = createFileUploader("/upload");
    });

    it("Should start off with no files", function() {
        expect(uploader.pendingFiles().length).toBe(0);
    });

    it("Should have no complete files until started", function() {
        uploader.addFile(0, {name: "foo.jpg"}, 1);
        uploader.addFile(1, {name: "bar.jpg"}, 2);

        expect(uploader.pendingFiles().length).toBe(2);
        expect(uploader.completedFiles().length).toBe(0);
    });

    it("should call XHR and beforeUploadHook", function() {
        spyOn(uploader, "readFileWithHandler").andCallFake(function(file, handler) {
            handler({target: {result: "foo"}});
        });
        var fakeXhr = createFakeXhr();
        spyOn(uploader, "newXhr").andCallFake(function() {
            return fakeXhr;
        });
        spyOn(uploader, "beforeUploadHook");

        uploader.addFile(0, {name: "foo.jpg"}, 1);
        uploader.start();

        expect(uploader.beforeUploadHook).toHaveBeenCalled();
        expect(fakeXhr.open).toHaveBeenCalled();
        expect(fakeXhr.sendAsBinary).toHaveBeenCalled();
        expect(fakeXhr.upload.addEventListener).toHaveBeenCalled();
    });
});

function createFakeXhr() {
    var fakeXhr = {
        upload: {
            addEventListener: function() {
            }
        },
        open: function() {
        },
        sendAsBinary: function() {
        }};
    spyOn(fakeXhr.upload, "addEventListener");

    spyOn(fakeXhr, "open");
    spyOn(fakeXhr, "sendAsBinary");
    return fakeXhr;
}
