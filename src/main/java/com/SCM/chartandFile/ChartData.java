package com.SCM.chartandFile;

import com.nimbusds.jose.shaded.gson.Gson;

public class ChartData {

    public static class DataSet {

        private String label;
        private String borderColor;
        private String[] backgroundColor;
        private Object[] data;
        private String[] pointBackgroundColor;
        
        // Constructors, if necessary

        public String getLabel() {
            return label;
        }

        public String getBorderColor() {
            return borderColor;
        }

        public String[] getBackgroundColor() {
            return backgroundColor;
        }

        public Object[] getData() {
            return data;
        }

        public String[] getPointBackgroundColor() {
            return pointBackgroundColor;
        }

		public void setLabel(String label) {
			this.label = label;
		}

		public void setBorderColor(String borderColor) {
			this.borderColor = borderColor;
		}

		public void setBackgroundColor(String[] backgroundColor) {
			this.backgroundColor = backgroundColor;
		}

		public void setData(Object[] data) {
			this.data = data;
		}

		public void setPointBackgroundColor(String[] pointBackgroundColor) {
			this.pointBackgroundColor = pointBackgroundColor;
		}
    }

    private String[] labels;
    private DataSet[] datasets;

    // Constructors, if necessary

    public String[] getLabels() {
        return labels;
    }

    public DataSet[] getDatasets() {
        return datasets;
    }
    
    public void setLabels(String[] labels) {
		this.labels = labels;
	}

	public void setDatasets(DataSet[] datasets) {
		this.datasets = datasets;
	}

	public String toJson() {
        return new Gson().toJson(this);
    }
}



