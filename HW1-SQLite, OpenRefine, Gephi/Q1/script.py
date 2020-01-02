import http.client
import json
import time
import timeit
import sys
import collections
from pygexf.gexf import *


#
# implement your data retrieval code here
#
api_key = sys.argv[1]
host =  "www.rebrickable.com"
conn = http.client.HTTPSConnection(host)
info = "https://rebrickable.com/api/v3/lego/sets/?key=" + api_key + \
       "&page=1&page_size=280&ordering=-num_parts%2Cid"
conn.request("GET", info)
r1 = conn.getresponse()
# print(r1.status, r1.reason)

data = json.load(r1)
# print(len(data['results']))
# print(data['results'][0]['num_parts'])
# print(data['results'][-1]['num_parts'])
min_num_parts = data['results'][-1]['num_parts']
sets_info = []
n = len(data['results'])
for i in range(n):
    temp = {}
    temp['name'] = data['results'][i]['name']
    temp['set_num'] = data['results'][i]['set_num']
    sets_info.append(temp)
# print(n, len(sets_info))
# print(sets_info[0])
# print(data['results'][0])

# Q1.1b
parts = []
for i in range(n):
    info = "https://rebrickable.com/api/v3/lego/sets/" + \
    data['results'][i]['set_num'] + "/parts/?key=" + api_key + \
       "&page=1&page_size=1000"    
    conn.request("GET", info)
    r2 = conn.getresponse()
    # print(r2.status, r2.reason)
    data2 = json.load(r2)
    data2['results'] = sorted(data2['results'], key=lambda x: x['quantity'], reverse=True)
    for j in range(len(data2['results'])):
        if j >= 20:
            break
        # print(data2['results'][j])
        temp = {}
        temp['id'] = data2['results'][j]['part']['part_num'] + "_" + \
                     data2['results'][j]['color']['rgb']
        temp['color'] = data2['results'][j]['color']['rgb']
        temp['quantity'] = data2['results'][j]['quantity']
        temp['name'] =  data2['results'][j]['part']['name']
        temp['number'] = data2['results'][j]['part']['part_num']
        temp['set_id'] = data['results'][i]['set_num']
        parts.append(temp)
        # print(temp)

# print(len(parts))


# complete auto grader functions for Q1.1.b,d
def min_parts():
    """
    Returns an integer value
    """
    # you must replace this with your own value
    return min_num_parts

def lego_sets():
    """
    return a list of lego sets.
    this may be a list of any type of values
    but each value should represent one set

    e.g.,
    biggest_lego_sets = lego_sets()
    print(len(biggest_lego_sets))
    > 280
    e.g., len(my_sets)
    """
    # you must replace this line and return your own list
    return sets_info

def gexf_graph():
    """
    return the completed Gexf graph object
    """
    # you must replace these lines and supply your own graph
    gexf = Gexf("Yuanxun Shao", "hw1 q1")
    graph = gexf.addGraph("undirected", "static", "hw1 q1 graph")
    attr_node = graph.addNodeAttribute(title = "Type", type = 'string')
    # add each set as a node
    for i in range(n):
        set_id = sets_info[i]['set_num']
        if graph.nodeExists(set_id):
            continue
        temp = graph.addNode(id = set_id, label =
                sets_info[i]['name'],  r="0", g="0", b="0")
        temp.addAttribute(attr_node, 'set')

    # add each part as a node
    for part in parts:
        if graph.nodeExists(part['id']):
            graph.addEdge(id = part['set_id']+part['id'], source = part['set_id'],\
                      target = part['id'], weight = part['quantity'])
            continue
        temp = graph.addNode(id = part['id'], label = part['name'], \
               r = str(int(part['color'][:2], 16)), \
               g = str(int(part['color'][2:4], 16)), \
               b = str(int(part['color'][4:], 16)))
        temp.addAttribute(attr_node, 'part')

        # add edge between each part and set(s) it belongs to
        graph.addEdge(id = part['set_id']+part['id'], source = part['set_id'],\
                      target = part['id'], weight = part['quantity'])

    output_file = open('bricks_graph.gexf', 'wb')
    gexf.write(output_file)

    return graph

# gexf_graph()
# complete auto-grader functions for Q1.2.d

def avg_node_degree():
    """
    hardcode and return the average node degree
    (run the function called “Average Degree”) within Gephi
    """
    # you must replace this value with the avg node degree
    return 5.401

def graph_diameter():
    """
    hardcode and return the diameter of the graph
    (run the function called “Network Diameter”) within Gephi
    """
    # you must replace this value with the graph diameter
    return 8

def avg_path_length():
    """
    hardcode and return the average path length
    (run the function called “Avg. Path Length”) within Gephi
    :return:
    """
    # you must replace this value with the avg path length
    return 4.421
