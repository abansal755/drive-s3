import FileCannotBeViewed from "./FileViewer/FileCannotBeViewed";
import TextViewer from "./FileViewer/TextViewer";

const FileViewer = ({
	file,
	mimeType,
	isViewerOpen,
	onViewerOpen,
	onViewerClose,
}) => {
	if (mimeType.startsWith("text") && file.sizeInBytes <= 5 * 1000 * 1000)
		return (
			<TextViewer
				file={file}
				isViewerOpen={isViewerOpen}
				onViewerOpen={onViewerOpen}
				onViewerClose={onViewerClose}
			/>
		);
	else
		return (
			<FileCannotBeViewed
				file={file}
				isViewerOpen={isViewerOpen}
				onViewerOpen={onViewerOpen}
				onViewerClose={onViewerClose}
			/>
		);
};

export default FileViewer;
